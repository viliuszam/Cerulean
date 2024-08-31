import React, { createContext, useState, useEffect, useContext, useCallback } from 'react';
import WebSocketManager from '../managers/WebSocketManager';
import axios from 'axios';

const WebSocketContext = createContext(null);

export const WebSocketProvider = ({ children }) => {
  const [notifications, setNotifications] = useState([]);
  const [isConnected, setIsConnected] = useState(false);
  const [currentUser, setCurrentUser] = useState(null);

  const refreshUserData = useCallback(async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get('http://localhost:8080/api/auth/user', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setCurrentUser(response.data);
      // Dispatch a custom event when user data is updated
      window.dispatchEvent(new CustomEvent('userDataUpdated', { detail: response.data }));
    } catch (error) {
      console.error('Error refreshing user data:', error);
    }
  }, []);

  const addNotification = useCallback((newNotification) => {
    const id = Date.now();
    setNotifications((prevNotifications) => [
      ...prevNotifications,
      { ...newNotification, id, createdAt: Date.now() }
    ]);

    if (newNotification.type === 'BALANCE_INCREMENT' || newNotification.type === 'BALANCE_DECREMENT') {
      refreshUserData();
    }

    if (newNotification.auctionId) {
      window.dispatchEvent(new CustomEvent('auctionUpdate', { detail: newNotification.auctionId.toString() }));
    }
  }, [refreshUserData]);

  const connect = useCallback(() => {
    const token = localStorage.getItem('token');
    if (!WebSocketManager.isConnected() && token) {
      WebSocketManager.connect(
        token,
        addNotification,
        () => setIsConnected(true),
        (error) => console.error('WebSocket error:', error)
      ).catch(error => console.error('Connection failed:', error));
    }
  }, [addNotification]);

  const disconnect = useCallback(() => {
    WebSocketManager.disconnect(() => {
      setNotifications([]);
      setIsConnected(false);
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
    <WebSocketContext.Provider value={{ notifications, isConnected, currentUser, connect, disconnect, removeNotification, refreshUserData }}>
      {children}
    </WebSocketContext.Provider>
  );
};

export const useWebSocket = () => useContext(WebSocketContext);