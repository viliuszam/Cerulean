import React, { useState, useEffect } from 'react';
import axios from 'axios';
import PageWithNavbar from '../components/PageWithNavbar';
import './AdminWithdrawalPage.css';

const AdminWithdrawalPage = () => {
  const [withdrawalRequests, setWithdrawalRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  useEffect(() => {
    const fetchPendingRequests = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/admin/pending', {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
        });
        setWithdrawalRequests(response.data);
        setLoading(false);
      } catch (err) {
        console.error('Error fetching pending requests:', err);
        setError('Error fetching pending requests.');
        setLoading(false);
      }
    };

    fetchPendingRequests();
  }, []);

  const handleApprove = async (id) => {
    try {
      const response = await axios.post(`http://localhost:8080/api/admin/approve-withdrawal/${id}`, null, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
      });
      setSuccessMessage(response.data);
      setWithdrawalRequests(withdrawalRequests.filter(request => request.id !== id));
    } catch (err) {
      console.error('Error approving withdrawal request:', err);
      setError('Error approving withdrawal request.');
    }
  };

  const handleDeny = async (id) => {
    const comment = prompt('Enter a reason for denial:');
    if (!comment) return;

    try {
      const response = await axios.post(`http://localhost:8080/api/admin/deny-withdrawal/${id}?comment=${encodeURIComponent(comment)}`, null, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
      });
      setSuccessMessage(response.data);
      setWithdrawalRequests(withdrawalRequests.filter(request => request.id !== id));
    } catch (err) {
      console.error('Error denying withdrawal request:', err);
      setError('Error denying withdrawal request.');
    }
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <PageWithNavbar>
      <div className="container mt-4">
        <h2>Admin Withdrawal Panel</h2>
        {error && <div className="alert alert-danger">{error}</div>}
        {successMessage && <div className="alert alert-success">{successMessage}</div>}
        <div className="table-responsive">
          <table className="table table-striped">
            <thead>
              <tr>
                <th>ID</th>
                <th>User</th>
                <th>Amount (EUR)</th>
                <th>Status</th>
                <th>Requested Date</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {withdrawalRequests.length > 0 ? (
                withdrawalRequests.map((request) => (
                  <tr key={request.id}>
                    <td>{request.id}</td>
                    <td>{request.user.username}</td>
                    <td>{request.amount}</td>
                    <td>{request.status}</td>
                    <td>{new Date(request.requestTime).toLocaleString()}</td>
                    <td>
                      <button className="btn btn-success btn-sm me-2" onClick={() => handleApprove(request.id)}>
                        Approve
                      </button>
                      <button className="btn btn-danger btn-sm" onClick={() => handleDeny(request.id)}>
                        Deny
                      </button>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="6">No pending withdrawal requests found.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </PageWithNavbar>
  );
};

export default AdminWithdrawalPage;
