import React from 'react';
import styled from 'styled-components';
import { useWebSocket } from '../context/WebSocketContext';
import NotificationItem from './NotificationItem';

const NotificationContainer = styled.div`
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1000;
`;

const Notification = () => {
  const { notifications, removeNotification } = useWebSocket();

  return (
    <NotificationContainer>
      {notifications.map((notification) => (
        <NotificationItem
          key={notification.id}
          notification={notification}
          onRemove={removeNotification}
        />
      ))}
    </NotificationContainer>
  );
};

export default Notification;