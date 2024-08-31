import React, { useState, useEffect } from 'react';
import { loadStripe } from '@stripe/stripe-js';
import { Elements, useStripe, useElements, CardElement } from '@stripe/react-stripe-js';
import axios from 'axios';
import { FaChevronLeft, FaChevronRight } from 'react-icons/fa';
import './DepositPage.css';

const stripePromise = loadStripe('pk_test_51PsSQHP4FN30HCPFJ5hCVYHtMliUx5NXEArkByiMGbWR00xeCSOe1o081MDyLiCr2bNMxsFRQlkc8eY3NvDKFn3800L8ISu5os');

const DepositForm = ({ onDepositSuccess }) => {
  const [depositAmount, setDepositAmount] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const stripe = useStripe();
  const elements = useElements();

  const handleDepositSubmit = async (event) => {
    event.preventDefault();
    if (!stripe || !elements) {
      return;
    }
    setLoading(true);
    setError('');
    setSuccessMessage('');

    try {
      const response = await axios.post(`http://localhost:8080/api/payments/deposit?amount=${depositAmount}`, null, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      });
      const clientSecret = response.data.clientSecret;
      const result = await stripe.confirmCardPayment(clientSecret, {
        payment_method: {
          card: elements.getElement(CardElement),
        },
      });

      if (result.error) {
        setError(result.error.message);
      } else {
        if (result.paymentIntent.status === 'succeeded') {
          setSuccessMessage('Deposit successful! Your balance has been updated.');
          setTimeout(onDepositSuccess, 2000); // takes a sec for the webhook to update
        }
      }
    } catch (error) {
      console.error('Error during deposit:', error);
      setError('An error occurred while processing the deposit. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="deposit-container">
      <h2>Deposit Funds</h2>
      <form onSubmit={handleDepositSubmit} className="deposit-form">
        <div className="form-group">
          <label htmlFor="depositAmount">Deposit Amount (EUR)</label>
          <input
            type="number"
            id="depositAmount"
            className="form-control"
            value={depositAmount}
            onChange={(e) => setDepositAmount(e.target.value)}
            min="1"
            step="0.01"
            required
          />
        </div>
        <div className="form-group">
          <label htmlFor="cardDetails">Card Details</label>
          <CardElement id="cardDetails" className="card-element" />
        </div>
        {error && <div className="alert alert-danger">{error}</div>}
        {successMessage && <div className="alert alert-success">{successMessage}</div>}
        <button type="submit" className="btn btn-primary" disabled={loading || !stripe}>
          {loading ? 'Processing...' : 'Deposit'}
        </button>
      </form>
    </div>
  );
};

const DepositHistory = ({ deposits, currentPage, totalPages, onPrevPage, onNextPage }) => {
  return (
    <div className="deposit-history">
      <h4>Deposit History</h4>
      <div className="deposit-history-container">
        {deposits.length > 0 ? (
          deposits.map((deposit, index) => (
            <div key={index} className="deposit-entry">
              <strong>€{deposit.amount.toFixed(2)}</strong> at {new Date(deposit.timestamp).toLocaleString()}
            </div>
          ))
        ) : (
          <p>No deposits found.</p>
        )}
      </div>
      {totalPages > 1 && (
        <div className="deposit-pagination">
          <button 
            onClick={onPrevPage} 
            disabled={currentPage === 1}
            className="btn btn-sm btn-outline-secondary"
          >
            <FaChevronLeft />
          </button>
          <span className="mx-2">
            Page {currentPage} of {totalPages}
          </span>
          <button 
            onClick={onNextPage} 
            disabled={currentPage === totalPages}
            className="btn btn-sm btn-outline-secondary"
          >
            <FaChevronRight />
          </button>
        </div>
      )}
    </div>
  );
};

const DepositPage = () => {
  const [deposits, setDeposits] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const depositsPerPage = 5;

  const fetchDeposits = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/payments/deposits', {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      });
      const sortedDeposits = response.data.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
      setDeposits(sortedDeposits);
      setTotalPages(Math.ceil(sortedDeposits.length / depositsPerPage));
    } catch (error) {
      console.error('Error fetching deposits:', error);
    }
  };

  useEffect(() => {
    fetchDeposits();
  }, []);

  const handlePrevPage = () => {
    setCurrentPage((prev) => Math.max(prev - 1, 1));
  };

  const handleNextPage = () => {
    setCurrentPage((prev) => Math.min(prev + 1, totalPages));
  };

  const currentDeposits = deposits.slice(
    (currentPage - 1) * depositsPerPage,
    currentPage * depositsPerPage
  );

  return (
      <div className="container mt-4">
        <div className="deposit-page-content">
          <Elements stripe={stripePromise}>
            <DepositForm onDepositSuccess={fetchDeposits} />
          </Elements>
          <DepositHistory
            deposits={currentDeposits}
            currentPage={currentPage}
            totalPages={totalPages}
            onPrevPage={handlePrevPage}
            onNextPage={handleNextPage}
          />
        </div>
      </div>
  );
};

export default DepositPage;