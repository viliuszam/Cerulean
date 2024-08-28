import React, { useState, useEffect } from 'react';
import axios from 'axios';
import PageWithNavbar from '../components/PageWithNavbar';

const WithdrawalPage = () => {
  const [isConnected, setIsConnected] = useState(false);
  const [withdrawalAmount, setWithdrawalAmount] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

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
      window.location.href = response.data; // redirect to Stripe onboarding
    } catch (err) {
      setError('Error connecting to Stripe. Please try again later.');
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
    } catch (error) {
     // console.error('Error submitting withdrawal request:', error);
      setError(error.response.data);
    } finally {
      setLoading(false);
    }
  };

  return (
    <PageWithNavbar>
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
          )}
        </div>
      </div>
    </PageWithNavbar>
  );
};

export default WithdrawalPage;
