import React from 'react';
import { useWebSocket } from '../context/WebSocketContext';

const Notification = () => {
    const { notifications } = useWebSocket();

    return (
        <div>
            <h2>Notifications</h2>
            {notifications.map((notification, index) => (
                <div key={index}>{notification.message}</div>
            ))}
        </div>
    );
};

export default Notification;