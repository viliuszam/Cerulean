import React from 'react';
import Navbar from '../components/Navbar';
import styled from 'styled-components';

const Container = styled.div`
    padding: 2rem;
`;

const DashboardPage = () => {
    return (
        <>
            <Navbar />
            <Container>
                <h1>Welcome to the Dashboard</h1>
                {/* stuff */}
            </Container>
        </>
    );
};

export default DashboardPage;