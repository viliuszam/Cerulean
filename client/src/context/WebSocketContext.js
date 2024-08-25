import React, { createContext, useState, useEffect, useContext, useCallback } from 'react';
import WebSocketManager from '../managers/WebSocketManager';
import axios from 'axios';

const WebSocketContext = createContext(null);

export const WebSocketProvider = ({ children }) => {
  const [notifications, setNotifications] = useState([]);
  const [isConnected, setIsConnected] = useState(false);

  const refreshAuctionData = useCallback(async (auctionId) => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(`http://localhost:8080/api/auctions/${auctionId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      console.log('Refreshed auction data:', response.data);
      return response.data;
    } catch (error) {
      console.error('Error refreshing auction data:', error);
    }
  }, []);

  const addNotification = useCallback((newNotification) => {
    console.log('Received new notification:', newNotification);
    const id = Date.now();
    setNotifications((prevNotifications) => [
      ...prevNotifications,
      { ...newNotification, id, createdAt: Date.now() }
    ]);

    if (newNotification.auctionId) {
      console.log('Dispatching auctionUpdate event for ID:', newNotification.auctionId);
      window.dispatchEvent(new CustomEvent('auctionUpdate', { detail: newNotification.auctionId.toString() }));
    }
  }, []);

  const connect = useCallback(() => {
    const token = localStorage.getItem('token');
    if (!WebSocketManager.isConnected() && token) {
      console.log('Connecting to WebSocket');
      WebSocketManager.connect(
        token,
        addNotification,
        () => {
          console.log('WebSocket connection established.');
          setIsConnected(true);
        },
        (error) => console.error('WebSocket error:', error)
      ).catch(error => console.error('Connection failed:', error));
    }
  }, [addNotification]);

  const disconnect = useCallback(() => {
    WebSocketManager.disconnect(() => {
      setNotifications([]);
      setIsConnected(false);
      console.log('WebSocket connection terminated.');
    });
  }, []);

  const removeNotification = useCallback((id) => {
    setNotifications((prevNotifications) =>
      prevNotifications.filter((notification) => notification.id !== id)
    );
  }, []);

  useEffect(() => {
    connect();
    return () => {
      disconnect();
    };
  }, [connect, disconnect]);

  useEffect(() => {
    const handleFocus = () => {
      const token = localStorage.getItem('token');
      if (token && !WebSocketManager.isConnected()) {
        connect();
      }
    };

    window.addEventListener('focus', handleFocus);
    return () => window.removeEventListener('focus', handleFocus);
  }, [connect]);

  return (
    <WebSocketContext.Provider value={{ notifications, isConnected, connect, disconnect, removeNotification, refreshAuctionData }}>
      {children}
    </WebSocketContext.Provider>
  );
};

export const useWebSocket = () => useContext(WebSocketContext);