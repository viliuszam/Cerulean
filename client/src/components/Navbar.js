import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { Icon } from '@iconify/react';
import homeIcon from '@iconify/icons-mdi/home';
import accountCircleIcon from '@iconify/icons-mdi/account-circle';
import logoutIcon from '@iconify/icons-mdi/logout';
import heartIcon from '@iconify/icons-mdi/heart';
import cartIcon from '@iconify/icons-mdi/cart';
import gavelIcon from '@iconify/icons-mdi/gavel';
import walletIcon from '@iconify/icons-mdi/wallet';
import { AuthContext } from '../context/AuthContext';

const NavbarContainer = styled.div`
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.5rem 1rem;
    background-color: #007acc;
    color: white;
    flex-wrap: wrap;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 1000;
`;

const NavLeft = styled.div`
    display: flex;
    align-items: center;
`;

const NavItem = styled(Link)`
    margin: 0 1rem;
    display: flex;
    align-items: center;
    text-decoration: none;
    color: white;

    & > svg {
        margin-right: 0.5rem;
    }

    &:hover {
        opacity: 0.8;
    }
`;

const NavRight = styled.div`
    display: flex;
    align-items: center;
`;

const UserInfo = styled.div`
    display: flex;
    align-items: center; // Changed to align items horizontally
`;

// Updated to make the logout button visually consistent
const LogoutButton = styled.button`
    background: transparent;
    border: 1px solid white;
    color: white;
    display: flex;
    align-items: center;
    cursor: pointer;
    font-size: 1rem;
    padding: 0.5rem 1rem;
    border-radius: 5px;

    &:hover {
        opacity: 0.8;
    }

    & > svg {
        margin-right: 0.5rem;
    }
`;

const Brand = styled.div`
    display: flex;
    align-items: center;
    font-size: 1.5rem;
    font-weight: bold;

    & > svg {
        margin-left: 0.5rem;
    }
`;

const BalanceDisplay = styled.div`
    margin-left: 1rem;
    color: white;
    font-size: 1rem;
`;

const Navbar = () => {
    const { user, logout } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <NavbarContainer>
            <NavLeft>
                <Brand>
                    Cerulean
                    <Icon icon={heartIcon} color="#ffffff" width="24" height="24" />
                    <Icon icon={cartIcon} color="#ffffff" width="24" height="24" />
                </Brand>
                <NavItem to="/auctions">
                    <Icon icon={homeIcon} width="24" height="24" />
                    Auctions
                </NavItem>
                <NavItem to="/bids">
                    <Icon icon={gavelIcon} width="24" height="24" />
                    My Bids
                </NavItem>
                <NavItem to="/wallet">
                    <Icon icon={walletIcon} width="24" height="24" />
                    Wallet
                </NavItem>
            </NavLeft>
            <NavRight>
                <UserInfo>
                    <NavItem to="/profile">
                        <Icon icon={accountCircleIcon} width="24" height="24" />
                        {user ? user.realName : ''}
                    </NavItem>
                    {user && user.balance && (
                        <BalanceDisplay>
                            Balance: €{user.balance.toFixed(2)}
                        </BalanceDisplay>
                    )}
                    <LogoutButton onClick={handleLogout}>
                        <Icon icon={logoutIcon} width="24" height="24" />
                        Logout
                    </LogoutButton>
                </UserInfo>
            </NavRight>
        </NavbarContainer>
    );
};

export default Navbar;
