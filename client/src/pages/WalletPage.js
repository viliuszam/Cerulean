import React, { useState } from 'react';
import DepositPage from './DepositPage';
import WithdrawalPage from './WithdrawalPage';
import './WalletPage.css'; // Import a new CSS file for WalletPage
import PageWithNavbar from '../components/PageWithNavbar';

const WalletPage = () => {
  const [activeTab, setActiveTab] = useState('deposit'); // State to manage active tab

  return (
    <PageWithNavbar>
      <div className="wallet-container">
        <div className="tab-labels">
          <div
            className={`tab-label ${activeTab === 'deposit' ? 'active' : ''}`}
            onClick={() => setActiveTab('deposit')}
          >
            Deposit
          </div>
          <div
            className={`tab-label ${activeTab === 'withdraw' ? 'active' : ''}`}
            onClick={() => setActiveTab('withdraw')}
          >
            Withdraw
          </div>
        </div>

        <div className="tab-content">
          {activeTab === 'deposit' ? <DepositPage /> : <WithdrawalPage />}
        </div>
      </div>
    </PageWithNavbar>
  );
};

export default WalletPage;
