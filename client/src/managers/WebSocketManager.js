import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

let stompClient = null;
let isConnected = false;
let connectionPromise = null;

const WebSocketManager = {
  connect: (token, onMessageReceived, onConnected, onError) => {
    if (connectionPromise) {
      return connectionPromise;
    }

    if (!token) {
      console.log('No token found, not connecting.');
      return Promise.reject('No token');
    }

    connectionPromise = new Promise((resolve, reject) => {
      const socket = new SockJS('http://localhost:8080/ws');
      stompClient = Stomp.over(socket);

      stompClient.connect(
        { Authorization: `Bearer ${token}` },
        (frame) => {
          console.log('Connected: ' + frame);
          isConnected = true; // Update the connection status
          onConnected(stompClient);
          
          // Ensure subscription happens only after connection is established
          stompClient.subscribe('/user/topic/notifications', (notification) => {
            console.log('Received notification:', notification);
            onMessageReceived(JSON.parse(notification.body));
          });

          resolve(stompClient);
        },
        (error) => {
          console.error('STOMP error:', error);
          isConnected = false; // Update the connection status
          onError(error);
          connectionPromise = null;
          reject(error);
        }
      );
    });

    return connectionPromise;
  },

  disconnect: (onDisconnected) => {
    if (stompClient && stompClient.connected) {
      stompClient.disconnect(() => {
        console.log('Disconnected');
        isConnected = false; // Update the connection status
        connectionPromise = null;
        onDisconnected();
      });
    }
  },

  isConnected: () => isConnected,
};

export default WebSocketManager;