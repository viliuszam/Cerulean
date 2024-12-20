import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, AuthContext } from './context/AuthContext';
import { WebSocketProvider } from './context/WebSocketContext';
import SignupPage from './pages/SignupPage';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import AuctionCreationPage from './pages/CreateAuctionPage';
import ProtectedRoute from './components/ProtectedRoute';
import AuctionsIndexPage from './pages/AuctionsIndexPage';
import MyBidsPage from './pages/MyBidsPage';
import AuctionDetailPage from './pages/AuctionDetailPage';
import AdminWithdrawalPage from './pages/AdminWithdrawalPage';
import AdminRoute from './components/AdminRoute';
import WalletPage from './pages/WalletPage';
import DeliveryPage from './pages/DeliveryPage';
import DeliveryDetailPage from './pages/DeliveryDetailPage';

const App = () => {
    return (
        <WebSocketProvider>
        <AuthProvider>
            <Router>
                <Routes>
                    <Route path="/login" element={
                        <AuthContext.Consumer>
                            {({ user }) => (user ? <Navigate to="/dashboard" /> : <LoginPage />)}
                        </AuthContext.Consumer>
                    } />
                    <Route path="/signup" element={
                        <AuthContext.Consumer>
                            {({ user }) => (user ? <Navigate to="/dashboard" /> : <SignupPage />)}
                        </AuthContext.Consumer>
                    } />
                    <Route path="/dashboard" element={<ProtectedRoute><DashboardPage /></ProtectedRoute>} />
                    <Route path="/auctions/create" element={<ProtectedRoute><AuctionCreationPage /></ProtectedRoute>} />
                    <Route path="/auctions" element={<ProtectedRoute><AuctionsIndexPage /></ProtectedRoute>} />
                    <Route path="/bids" element={<ProtectedRoute><MyBidsPage /></ProtectedRoute>} />
                    <Route path="/delivery" element={<ProtectedRoute><DeliveryPage /></ProtectedRoute>} />
                    <Route path="/auction/:id" element={<ProtectedRoute><AuctionDetailPage /></ProtectedRoute>} />
                    <Route path="/delivery/:deliveryId" element={<ProtectedRoute><DeliveryDetailPage /></ProtectedRoute>} />
                    <Route path="/admin/withdrawals" element={<AdminRoute><AdminWithdrawalPage /></AdminRoute>} />
                    <Route path="/wallet" element={<ProtectedRoute><WalletPage /></ProtectedRoute>} />
                    <Route path="/" element={<Navigate to="/dashboard" />} />
                </Routes>
            </Router>
        </AuthProvider>
        </WebSocketProvider>
    );
};

export default App;