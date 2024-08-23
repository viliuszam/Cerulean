import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import PageWithNavbar from '../components/PageWithNavbar';
import { FaTrophy, FaArrowDown, FaCheck, FaTimes } from 'react-icons/fa';

const MyBidsPage = () => {
    const [bids, setBids] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        fetchUserBids();
    }, []);

    const fetchUserBids = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get('http://localhost:8080/api/bids/me', {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            setBids(response.data || []);
        } catch (error) {
            console.error('Error fetching user bids:', error);
        }
    };

    const handleRowClick = (auctionId) => {
        navigate(`/auction/${auctionId}`);
    };

    const calculateTimeRemaining = (endDate) => {
        const now = new Date();
        const end = new Date(endDate);
        const timeLeft = end - now;

        const days = Math.floor(timeLeft / (1000 * 60 * 60 * 24));
        const hours = Math.floor((timeLeft / (1000 * 60 * 60)) % 24);
        const minutes = Math.floor((timeLeft / (1000 * 60)) % 60);
        const seconds = Math.floor((timeLeft / 1000) % 60);

        return { days, hours, minutes, seconds, expired: timeLeft <= 0 };
    };

    const [timers, setTimers] = useState({});

    useEffect(() => {
        const interval = setInterval(() => {
            const newTimers = {};
            bids.forEach((bid) => {
                newTimers[bid.auctionId] = calculateTimeRemaining(bid.endDate);
            });
            setTimers(newTimers);
        }, 1000);

        return () => clearInterval(interval);
    }, [bids]);

    const getStatusIcon = (status) => {
        switch (status) {
            case 'TOP_BIDDER':
                return <FaTrophy style={{ color: 'gold' }} />;
            case 'OUTBID':
                return <FaArrowDown style={{ color: 'red' }} />;
            case 'WON':
                return <FaCheck style={{ color: 'green' }} />;
            case 'LOST':
                return <FaTimes style={{ color: 'gray' }} />;
            default:
                return null;
        }
    };

    const formatStatus = (status) => {
        return status.toLowerCase().replace('_', ' ');
    };

    return (
        <PageWithNavbar>
            <div className="my-bids-container with-navbar">
                <div className="container mt-4" style={{ maxWidth: '95%', margin: '0 auto', textAlign: 'center' }}>
                    <h1 className="mb-4">My Bids</h1>
                    {bids.length > 0 ? (
                        <div className="table-responsive">
                            <table className="table table-hover" style={{ border: '1px solid #ddd', borderCollapse: 'collapse', margin: '0 auto' }}>
                                <thead style={{ borderBottom: '2px solid #ddd', textAlign: 'center' }}>
                                    <tr>
                                        <th>Item</th>
                                        <th>Description</th>
                                        <th>Time Remaining</th>
                                        <th>Highest Bid</th>
                                        <th>Your Bid</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {bids.map((bid) => {
                                        const { days, hours, minutes, seconds, expired } = timers[bid.auctionId] || {};
                                        return (
                                            <tr
                                                key={bid.auctionId}
                                                onClick={() => handleRowClick(bid.auctionId)}
                                                style={{ cursor: 'pointer', borderBottom: '1px solid #ddd' }}
                                            >
                                                <td style={{ textAlign: 'center', padding: '10px 0' }}>
                                                    <div className="d-flex flex-column align-items-center">
                                                        <img
                                                            src={bid.imageUrls.length > 0
                                                                ? `http://localhost:8080/${bid.imageUrls[0]}`
                                                                : 'http://localhost:8080/uploads/unknown.png'}
                                                            alt={bid.itemName}
                                                            style={{ width: '100px', height: 'auto', marginLeft: '5px', borderRadius: '5px' }}
                                                        />
                                                        <br />
                                                        <span style={{ textAlign: 'center' }}>{bid.itemName}</span>
                                                    </div>
                                                </td>
                                                <td style={{ padding: '10px' }}>{bid.description}</td>
                                                <td style={{ padding: '10px' }}>
                                                    {expired ? (
                                                        <span style={{ color: 'red' }}>Auction Ended</span>
                                                    ) : (
                                                        <span>
                                                            {days}d {hours}h {minutes}m {seconds}s
                                                        </span>
                                                    )}
                                                </td>
                                                <td style={{ padding: '10px' }}>€{bid.highestBid.toFixed(2)}</td>
                                                <td style={{ padding: '10px' }}>€{bid.userBidAmount.toFixed(2)}</td>
                                                <td style={{ padding: '10px', textTransform: 'capitalize' }}>
                                                    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                                        {getStatusIcon(bid.userBidStatus)}
                                                        <span style={{ marginLeft: '5px' }}>{formatStatus(bid.userBidStatus)}</span>
                                                    </div>
                                                </td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>
                        </div>
                    ) : (
                        <div className="no-bids-message" style={{ 
                            backgroundColor: '#f8f9fa', 
                            border: '1px solid #dee2e6', 
                            borderRadius: '5px', 
                            padding: '20px', 
                            marginTop: '20px' 
                        }}>
                            <h3>You haven't placed any bids yet.</h3>
                            <p>Start bidding on items to see them appear here!</p>
                        </div>
                    )}
                </div>
            </div>
        </PageWithNavbar>
    );
};

export default MyBidsPage;