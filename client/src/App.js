import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, AuthContext } from './context/AuthContext';
import SignupPage from './pages/SignupPage';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import ProtectedRoute from './components/ProtectedRoute';

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
                    <Route path="/" element={<Navigate to="/dashboard" />} />
                </Routes>
            </Router>
        </AuthProvider>
    );
};

export default App;
