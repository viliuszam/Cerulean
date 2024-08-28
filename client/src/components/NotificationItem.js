import React, { useState, useEffect } from 'react';
import { FaGavel, FaInfoCircle } from 'react-icons/fa';
import styled from 'styled-components';
import { useNavigate } from 'react-router-dom';

const NotificationWrapper = styled.div`
  display: flex;
  align-items: center;
  background-color: rgba(173, 216, 230, 0.8);
  border: 1px solid #87CEFA;
  border-radius: 8px;
  padding: 10px;
  margin-bottom: 10px;
  max-width: 300px;
  animation: ${props => props.$isExiting ? 'fadeOutDown 0.5s' : 'fadeInUp 0.5s'};
  animation-fill-mode: forwards;
  cursor: pointer;

  @keyframes fadeInUp {
    from {
      opacity: 0;
      transform: translateY(20px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  @keyframes fadeOutDown {
    from {
      opacity: 1;
      transform: translateY(0);
    }
    to {
      opacity: 0;
      transform: translateY(20px);
    }
  }
`;

const IconWrapper = styled.div`
  margin-right: 10px;
  font-size: 1.2em;
`;

const NotificationContent = styled.div`
  flex: 1;
`;

const NotificationItem = ({ notification, onRemove }) => {
  const [isExiting, setIsExiting] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const exitTimeout = setTimeout(() => {
      setIsExiting(true);
    }, 4500);

    const removeTimeout = setTimeout(() => {
      onRemove(notification.id);
    }, 5000);

    return () => {
      clearTimeout(exitTimeout);
      clearTimeout(removeTimeout);
    };
  }, [notification.id, onRemove]);

  const handleClick = () => {
    if (notification.auctionId) {
      navigate(`/auction/${notification.auctionId}`);
      onRemove(notification.id);
    }
  };

  const getIcon = () => {
    switch (notification.type) {
      case 'OUTBID':
        return <FaGavel />;
      case 'AUCTION_ENDED':
        return <FaInfoCircle />;
      default:
        return null;
    }
  };

  return (
    <NotificationWrapper $isExiting={isExiting} onClick={handleClick}>
      <IconWrapper>{getIcon()}</IconWrapper>
      <NotificationContent>{notification.message}</NotificationContent>
    </NotificationWrapper>
  );
};

export default NotificationItem;