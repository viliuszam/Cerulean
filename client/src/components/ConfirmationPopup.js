import React from 'react';
import { FaCheckCircle, FaTimesCircle } from 'react-icons/fa';
import './ConfirmationPopup.css';

const ConfirmationPopup = ({ message, onConfirm, onCancel }) => {
  return (
    <div className="popup-overlay">
      <div className="popup-content">
        <p>{message}</p>
        <div className="popup-buttons">
          <button className="confirm-button" onClick={onConfirm}>
            <FaCheckCircle /> Yes
          </button>
          <button className="cancel-button" onClick={onCancel}>
            <FaTimesCircle /> No
          </button>
        </div>
      </div>
    </div>
  );
};

export default ConfirmationPopup;