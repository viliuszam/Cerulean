import React, { createContext, useState, useEffect, useContext, useCallback } from 'react';
import WebSocketManager from '../managers/WebSocketManager';

const WebSocketContext = createContext(null);

export const WebSocketProvider = ({ children }) => {
  const [notifications, setNotifications] = useState([]);
  const [isConnected, setIsConnected] = useState(false);

  const connect = useCallback(() => {
    const token = localStorage.getItem('token');
    if (!WebSocketManager.isConnected() && token) {
      WebSocketManager.connect(
        token,
        (newNotification) => setNotifications((prevNotifications) => [...prevNotifications, newNotification]),
        () => {
          console.log('WebSocket connection established.');
          setIsConnected(true);
        },
        (error) => console.error('WebSocket error:', error)
      ).catch(error => console.error('Connection failed:', error));
    }
  }, []);

  const disconnect = useCallback(() => {
    WebSocketManager.disconnect(() => {
      setNotifications([]);
      setIsConnected(false);
      console.log('WebSocket connection terminated.');
    });
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
    <WebSocketContext.Provider value={{ notifications, isConnected, connect, disconnect }}>
      {children}
    </WebSocketContext.Provider>
  );
};

export const useWebSocket = () => useContext(WebSocketContext);