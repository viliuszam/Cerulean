import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { FaChevronLeft, FaChevronRight } from 'react-icons/fa';
import './WithdrawalPage.css';

const WithdrawalPage = () => {
  const [isConnected, setIsConnected] = useState(false);
  const [withdrawalAmount, setWithdrawalAmount] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [unlinking, setUnlinking] = useState(false);
  const [refreshHistory, setRefreshHistory] = useState(false);

  useEffect(() => {
    const checkStripeConnection = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/stripe/status', {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
        setIsConnected(response.data.connected);
      } catch (error) {
        console.error('Error checking Stripe connection status:', error);
      }
    };

    checkStripeConnection();
  }, []);

  const handleConnectStripe = async () => {
    try {
      const response = await axios.get(`http://localhost:8080/api/stripe/connect`, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      });
      window.location.href = response.data;
    } catch (err) {
      setError('Error connecting to Stripe. Please try again later.');
    }
  };

  const handleUnlinkStripe = async () => {
    setUnlinking(true);
    setError('');
    setSuccessMessage('');

    try {
      const response = await axios.get('http://localhost:8080/api/stripe/remove', {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      });

      if (response.data.deleted) {
        setSuccessMessage('Stripe account unlinked successfully.');
        setIsConnected(false);
      } else {
        setError('Failed to unlink Stripe account. Please try again.');
      }
    } catch (err) {
      console.error('Error unlinking Stripe account:', err);
      setError('Error unlinking Stripe account: ' + err.response?.data || err.message);
    } finally {
      setUnlinking(false);
    }
  };

  const handleWithdrawalSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);
    setError('');
    setSuccessMessage('');

    try {
      await axios.post(`http://localhost:8080/api/payments/withdraw?amount=${withdrawalAmount}`, null, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      });
      setSuccessMessage('Withdrawal request submitted successfully!');
      setRefreshHistory(prev => !prev);
    } catch (error) {
      setError(error.response?.data || 'Error submitting withdrawal request.');
    } finally {
      setLoading(false);
    }
  };

  return (
      <div className="container mt-4">
        <div className="withdrawal-container">
          <h2>Withdraw Funds</h2>
          {!isConnected ? (
            <div className="connect-stripe">
              <p>To withdraw funds, please connect your Stripe account first.</p>
              <button onClick={handleConnectStripe} className="btn btn-primary">
                Connect Stripe Account
              </button>
            </div>
          ) : (
            <div>
              <form onSubmit={handleWithdrawalSubmit} className="withdrawal-form">
                <div className="form-group">
                  <label htmlFor="withdrawalAmount">Withdrawal Amount (EUR)</label>
                  <input
                    type="number"
                    id="withdrawalAmount"
                    className="form-control"
                    value={withdrawalAmount}
                    onChange={(e) => setWithdrawalAmount(e.target.value)}
                    min="1"
                    step="0.01"
                    required
                  />
                </div>
                {error && <div className="alert alert-danger">{error}</div>}
                {successMessage && <div className="alert alert-success">{successMessage}</div>}
                <button type="submit" className="btn btn-primary" disabled={loading}>
                  {loading ? 'Processing...' : 'Request Withdrawal'}
                </button>
              </form>
              <WithdrawalHistory key={refreshHistory} />
              {isConnected && (
                <div className="unlink-stripe">
                  <button onClick={handleUnlinkStripe} className="btn btn-primary" disabled={unlinking}>
                    {unlinking ? 'Unlinking...' : 'Unlink Stripe account'}
                  </button>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
  );
};

const WithdrawalHistory = () => {
  const [withdrawals, setWithdrawals] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const withdrawalsPerPage = 5;

  useEffect(() => {
    fetchWithdrawals();
  }, []);

  const fetchWithdrawals = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await axios.get('http://localhost:8080/api/payments/withdrawal-requests', {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      });
      if (Array.isArray(response.data)) {
        setWithdrawals(response.data.sort((a, b) => new Date(b.requestTime) - new Date(a.requestTime)));
      } else {
        setWithdrawals([]);
      }
    } catch (error) {
      console.error('Error fetching withdrawal requests:', error);
      setError('Failed to fetch withdrawal history. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  const indexOfLastWithdrawal = currentPage * withdrawalsPerPage;
  const indexOfFirstWithdrawal = indexOfLastWithdrawal - withdrawalsPerPage;
  const currentWithdrawals = withdrawals.slice(indexOfFirstWithdrawal, indexOfLastWithdrawal);
  const totalPages = Math.ceil(withdrawals.length / withdrawalsPerPage);

  const handlePrevPage = () => setCurrentPage(prev => Math.max(prev - 1, 1));
  const handleNextPage = () => setCurrentPage(prev => Math.min(prev + 1, totalPages));

  if (loading) return <div>Loading withdrawal history...</div>;
  if (error) return <div className="alert alert-danger">{error}</div>;

  return (
    <div className="withdrawal-history">
      <h4>Withdrawal Request History</h4>
      <div className="withdrawal-history-container">
        {currentWithdrawals.length > 0 ? (
          currentWithdrawals.map((withdrawal, index) => (
            <div key={index} className="withdrawal-entry">
              <strong>€{withdrawal.amount.toFixed(2)}</strong> - {withdrawal.status}
              <br />
              Requested at: {new Date(withdrawal.requestTime).toLocaleString()}
              {withdrawal.adminComment && (
                <div className="admin-comment">
                  Admin comment: {withdrawal.adminComment}
                </div>
              )}
            </div>
          ))
        ) : (
          <p>No withdrawal requests found.</p>
        )}
      </div>
      {totalPages > 1 && (
        <div className="withdrawal-pagination">
          <button 
            onClick={handlePrevPage} 
            disabled={currentPage === 1}
            className="btn btn-sm btn-outline-secondary"
          >
            <FaChevronLeft />
          </button>
          <span className="mx-2">
            Page {currentPage} of {totalPages}
          </span>
          <button 
            onClick={handleNextPage} 
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

export default WithdrawalPage;