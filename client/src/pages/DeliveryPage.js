import React, { useState, useEffect } from 'react';
import axios from 'axios';
import PageWithNavbar from '../components/PageWithNavbar';
import { useNavigate } from 'react-router-dom';
import './DeliveryPage.css';;

const DeliveryPage = () => {
  const [activeTab, setActiveTab] = useState('buyer');
  const [deliveries, setDeliveries] = useState([]);

  const navigate = useNavigate();

  useEffect(() => {
    fetchDeliveries();
  }, [activeTab]);

  const fetchDeliveries = async () => {
    try {
      const response = await axios.get(`http://localhost:8080/api/delivery/me?role=${activeTab}`, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      });
      
      setDeliveries(response.data);
    } catch (error) {
      console.error('Error fetching deliveries:', error);
    }
  };

  const DeliveryList = ({ deliveries }) => {
    if (deliveries.length === 0) {
      return (
        <div className="no-deliveries-message" style={{
          backgroundColor: '#f8f9fa',
          border: '1px solid #dee2e6',
          borderRadius: '5px',
          padding: '20px',
          marginTop: '20px'
        }}>
          <h3>You don't have any {activeTab} deliveries yet.</h3>
          <p>{activeTab === 'buyer' 
            ? "Items you've purchased will appear here once they're ready for delivery." 
            : "Items you've sold will appear here once they're ready for delivery."}
          </p>
        </div>
      );
    }

    return (
      <table className="delivery-table">
        <thead>
          <tr>
            <th>Item Name</th>
            <th>Status</th>
            <th>Tracking Info</th>
            <th>{activeTab === 'buyer' ? 'Seller' : 'Buyer'}</th>
          </tr>
        </thead>
        <tbody>
          {deliveries.map((delivery) => (
            <tr key={delivery.id} onClick={() => navigate(`/delivery/${delivery.id}`)}
            style={{ cursor: 'pointer', borderBottom: '1px solid #ddd' }}  >
              <td>{delivery.itemName}</td>
              <td>
                <span className={`status-badge ${delivery.status.toLowerCase()}`}>
                  {delivery.status}
                </span>
              </td>
              <td>{delivery.trackingInfo || 'N/A'}</td>
              <td>{activeTab === 'buyer' ? delivery.sellerUsername : delivery.buyerUsername}</td>
            </tr>
          ))}
        </tbody>
      </table>
    );
  };

  return (
    <PageWithNavbar>
      <div className="delivery-container">
        <div className="tab-labels">
          <div
            className={`tab-label ${activeTab === 'buyer' ? 'active' : ''}`}
            onClick={() => setActiveTab('buyer')}
          >
            Buyer Deliveries
          </div>
          <div
            className={`tab-label ${activeTab === 'seller' ? 'active' : ''}`}
            onClick={() => setActiveTab('seller')}
          >
            Seller Deliveries
          </div>
        </div>
        <div className="tab-content">
          <DeliveryList deliveries={deliveries} />
        </div>
      </div>
    </PageWithNavbar>
  );
};

export default DeliveryPage;