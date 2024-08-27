import React, { useState } from 'react';
import { loadStripe } from '@stripe/stripe-js';
import { Elements, useStripe, useElements, CardElement } from '@stripe/react-stripe-js';
import axios from 'axios';
import PageWithNavbar from '../components/PageWithNavbar';
import './DepositPage.css';

const stripePromise = loadStripe('pk_test_51PsSQHP4FN30HCPFJ5hCVYHtMliUx5NXEArkByiMGbWR00xeCSOe1o081MDyLiCr2bNMxsFRQlkc8eY3NvDKFn3800L8ISu5os');

const DepositForm = () => {
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

const DepositPage = () => {
  return (
    <PageWithNavbar>
      <div className="container mt-4">
        <Elements stripe={stripePromise}>
          <DepositForm />
        </Elements>
      </div>
    </PageWithNavbar>
  );
};

export default DepositPage;