import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, AuthContext } from './context/AuthContext';
import SignupPage from './pages/SignupPage';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import AuctionCreationPage from './pages/CreateAuctionPage';
import ProtectedRoute from './components/ProtectedRoute';
import AuctionsIndexPage from './pages/AuctionsIndexPage';

const App = () => {
    return (
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
                    <Route path="/" element={<Navigate to="/dashboard" />} />
                </Routes>
            </Router>
        </AuthProvider>
    );
};

export default App;
