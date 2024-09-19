import React, { useState, useEffect, useCallback, useRef } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import { useWebSocket } from '../context/WebSocketContext';
import PageWithNavbar from '../components/PageWithNavbar';
import { Star, Send, Truck, Package, Check } from 'lucide-react';
import ConfirmationPopup from '../components/ConfirmationPopup';
import './DeliveryDetailPage.css';

const DeliveryDetailPage = () => {
  const { deliveryId } = useParams();
  const [delivery, setDelivery] = useState(null);
  const [message, setMessage] = useState('');
  const [rating, setRating] = useState(0);
  const [review, setReview] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showConfirmationPopup, setShowConfirmationPopup] = useState(false);
  const [trackingInfo, setTrackingInfo] = useState('');
  const { isConnected } = useWebSocket();
  const chatContainerRef = useRef(null);

  const fetchDelivery = useCallback(async () => {
    try {
      setLoading(true);
      const response = await axios.get(`http://localhost:8080/api/delivery/${deliveryId}`, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      });
      setDelivery(response.data);
      setTrackingInfo(response.data.trackingInfo || '');
      setLoading(false);
    } catch (error) {
      console.error('Error fetching delivery:', error);
      setError('Failed to fetch delivery details');
      setLoading(false);
    }
  }, [deliveryId]);

  useEffect(() => {
    fetchDelivery();
  }, [fetchDelivery]);

  useEffect(() => {
    if (chatContainerRef.current && !loading) {
      chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight;
    }
  }, [delivery, loading]);

  useEffect(() => {
    const handleDeliveryUpdate = (event) => {
      const notification = event.detail;
      if (notification.auctionId.toString() === deliveryId) {
        fetchDelivery();
      }
    };

    window.addEventListener('deliveryUpdate', handleDeliveryUpdate);

    return () => {
      window.removeEventListener('deliveryUpdate', handleDeliveryUpdate);
    };
  }, [deliveryId, fetchDelivery]);

  const sendMessage = async (e) => {
    e.preventDefault();
    if (!message.trim()) return;
    try {
      await axios.post(`http://localhost:8080/api/delivery/${deliveryId}/message`, message, {
        headers: { 
          Authorization: `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'text/plain'
        }
      });
      setMessage('');
      fetchDelivery();
    } catch (error) {
      console.error('Error sending message:', error);
    }
  };

  const updateTrackingInfo = async (e) => {
    e.preventDefault();
    try {
      await axios.put(`http://localhost:8080/api/delivery/${deliveryId}/tracking`, trackingInfo, {
        headers: { 
          Authorization: `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'text/plain'
        }
      });
      fetchDelivery();
    } catch (error) {
      console.error('Error updating tracking info:', error);
    }
  };

  const updateStatus = async (status) => {
    try {
      await axios.put(`http://localhost:8080/api/delivery/${deliveryId}/status`, status, {
        headers: { 
          Authorization: `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'text/plain'
        }
      });
      fetchDelivery();
    } catch (error) {
      console.error('Error updating status:', error);
    }
  };

  const confirmDelivery = () => {
    setShowConfirmationPopup(true);
  };

  const handleConfirmDelivery = async (confirmed) => {
    setShowConfirmationPopup(false);
    if (confirmed) {
      try {
        await axios.put(`http://localhost:8080/api/delivery/${deliveryId}/confirm`, {}, {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
        });
        fetchDelivery();
      } catch (error) {
        console.error('Error confirming delivery:', error);
      }
    }
  };

  const rateSeller = async () => {
    try {
      await axios.post(`http://localhost:8080/api/delivery/rate/${deliveryId}`, { rating, review }, {
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
      });
      fetchDelivery();
    } catch (error) {
      console.error('Error rating seller:', error);
    }
  };

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!delivery) return <div className="not-found">Delivery not found</div>;

  return (
    <PageWithNavbar>
      <div className="delivery-detail-container">
        <div className="websocket-status-indicator">
          {isConnected ? (
            <span className="status-connected">●</span>
          ) : (
            <span className="status-disconnected">●</span>
          )}
        </div>
        <div className="delivery-header">
          <h2>{delivery.itemName}</h2>
          <div className="status-badge">
            {delivery.status === 'PENDING' && <Package />}
            {delivery.status === 'IN_TRANSIT' && <Truck />}
            {delivery.status === 'DELIVERED' && <Check />}
            <span>{delivery.status}</span>
          </div>
        </div>
        <p className="tracking-info">Tracking Info: {delivery.trackingInfo || 'N/A'}</p>
        
        {delivery.seller && delivery.status !== 'DELIVERED' && (
          <div className="seller-actions">
            <form onSubmit={updateTrackingInfo}>
              <input 
                type="text" 
                value={trackingInfo}
                onChange={(e) => setTrackingInfo(e.target.value)}
                placeholder="Update tracking info" 
              />
              <button type="submit">Update Tracking</button>
            </form>
            <select onChange={(e) => updateStatus(e.target.value)} value={delivery.status}>
              <option value="">Select Status</option>
              <option value="PENDING">Pending</option>
              <option value="IN_TRANSIT">In Transit</option>
            </select>
          </div>
        )}

        {!delivery.seller && delivery.status === 'IN_TRANSIT' && (
          <button className="confirm-delivery-btn" onClick={confirmDelivery}>
            <Check /> Confirm Delivery
          </button>
        )}

        <div className="chat-container" ref={chatContainerRef}>
          {delivery.messages.map((msg) => (
            <div key={msg.id} className={`message ${msg.senderUsername === delivery.sellerUsername ? 'seller' : 'buyer'}`}>
              <div className="message-content">{msg.content}</div>
              <div className="message-info">{msg.senderUsername} - {new Date(msg.sentAt).toLocaleString()}</div>
            </div>
          ))}
        </div>

        <form onSubmit={sendMessage} className="message-input">
          <input 
            type="text" 
            value={message} 
            onChange={(e) => setMessage(e.target.value)}
            placeholder="Type a message" 
          />
          <button type="submit"><Send /></button>
        </form>

        {!delivery.seller && delivery.status === 'DELIVERED' && !delivery.userRating && (
          <div className="rating-container">
            <div className="star-rating">
              {[1, 2, 3, 4, 5].map((star) => (
                <Star
                  key={star}
                  onClick={() => setRating(star)}
                  fill={star <= rating ? 'gold' : 'none'}
                  stroke={star <= rating ? 'gold' : 'currentColor'}
                />
              ))}
            </div>
            <textarea 
              value={review} 
              onChange={(e) => setReview(e.target.value)} 
              placeholder="Write a review" 
            />
            <button className="submit-rating-btn" onClick={rateSeller}>Submit Rating</button>
          </div>
        )}

        {delivery.status === 'DELIVERED' && delivery.userRating && (
          <div className="user-rating">
            <h3>Your Rating</h3>
            <div className="star-rating">
              {[1, 2, 3, 4, 5].map((star) => (
                <Star
                  key={star}
                  fill={star <= delivery.userRating.rating ? 'gold' : 'none'}
                  stroke={star <= delivery.userRating.rating ? 'gold' : 'currentColor'}
                />
              ))}
            </div>
            <p>Review: {delivery.userRating.review}</p>
          </div>
        )}

        {showConfirmationPopup && (
          <ConfirmationPopup
            message="Are you sure you want to confirm delivery? This action is final and indicates that you have received the item."
            onConfirm={() => handleConfirmDelivery(true)}
            onCancel={() => handleConfirmDelivery(false)}
          />
        )}
      </div>
    </PageWithNavbar>
  );
};

export default DeliveryDetailPage;