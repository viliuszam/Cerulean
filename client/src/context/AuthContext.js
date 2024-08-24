import React, { createContext, useState, useEffect } from 'react';
import axios from 'axios';
import { useWebSocket } from './WebSocketContext';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const { connect, disconnect } = useWebSocket();

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            axios.get('http://localhost:8080/api/auth/user', {
                headers: { Authorization: `Bearer ${token}` }
            }).then(response => {
                setUser(response.data);
                connect();
                setLoading(false);
            }).catch(() => {
                setLoading(false);
            });
        } else {
            setLoading(false);
        }
    }, []);

    const login = (token, userData) => {
        localStorage.setItem('token', token);
        connect();
        setUser(userData);
    };

    const logout = () => {
        localStorage.removeItem('token');
        disconnect();
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, loading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export default AuthProvider;
