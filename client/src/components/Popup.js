import React from 'react';
import { FaCheckCircle, FaTimesCircle } from 'react-icons/fa';
import './Popup.css';

const Popup = ({ message, success, onClose }) => {
    return (
        <div className="popup-overlay">
            <div className="popup-content">
                {success ? (
                    <FaCheckCircle className="popup-icon success" />
                ) : (
                    <FaTimesCircle className="popup-icon error" />
                )}
                <p>{message}</p>
                <button className="btn btn-primary" onClick={onClose}>
                    OK
                </button>
            </div>
        </div>
    );
};

export default Popup;
