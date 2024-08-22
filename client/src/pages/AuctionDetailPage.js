import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import PageWithNavbar from '../components/PageWithNavbar';
import { FaCircle, FaArrowLeft, FaArrowRight } from 'react-icons/fa';
import './AuctionDetailPage.css';
import Popup from '../components/Popup';

const AuctionDetailPage = () => {
    const { id } = useParams();
    const [auction, setAuction] = useState(null);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);
    const [bidAmount, setBidAmount] = useState('');
    const [popupVisible, setPopupVisible] = useState(false);
    const [popupMessage, setPopupMessage] = useState('');
    const [popupSuccess, setPopupSuccess] = useState(false);
    const [agreementChecked, setAgreementChecked] = useState(false);

    useEffect(() => {
        fetchAuction();
    }, [id]);

    const fetchAuction = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get(`http://localhost:8080/api/auctions/${id}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            setAuction(response.data);
        } catch (error) {
            console.error('Error fetching auction:', error);
        }
    };

    const handleBidSubmit = async (event) => {
        event.preventDefault();
        if (!agreementChecked) {
            setPopupMessage('You must agree to the terms before placing a bid.');
            setPopupSuccess(false);
            setPopupVisible(true);
            return;
        }

        try {
            const token = localStorage.getItem('token');
            const response = await axios.post(
                `http://localhost:8080/api/bids/auction/${id}`,
                { amount: bidAmount },
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            if (response.data.success) {
                setPopupMessage('Your bid was placed successfully!');
                setPopupSuccess(true);
                fetchAuction(); 
            } else {
                setPopupMessage(response.data.message);
                setPopupSuccess(false);
            }
            setPopupVisible(true);
        } catch (error) {
            setPopupMessage('An error occurred while placing your bid. Please try again.');
            setPopupSuccess(false);
            setPopupVisible(true);
            console.error('Error placing bid:', error);
        }
    };

    const handlePopupClose = () => {
        setPopupVisible(false);
    };

    const getStatusIcon = (status) => {
        switch (status) {
            case 'IN_PROGRESS':
                return <FaCircle style={{ color: 'green' }} title="In Progress" />;
            case 'FINISHED':
                return <FaCircle style={{ color: 'blue' }} title="Finished" />;
            case 'CANCELED':
                return <FaCircle style={{ color: 'red' }} title="Canceled" />;
            default:
                return <FaCircle style={{ color: 'gray' }} title="Unknown Status" />;
        }
    };

    const handlePrevImage = () => {
        setCurrentImageIndex((prevIndex) =>
            prevIndex === 0 ? auction.imageUrls.length - 1 : prevIndex - 1
        );
    };

    const handleNextImage = () => {
        setCurrentImageIndex((prevIndex) =>
            prevIndex === auction.imageUrls.length - 1 ? 0 : prevIndex + 1
        );
    };

    if (!auction) {
        return (
            <PageWithNavbar>
                <div className="container mt-4 text-center">
                    <h1>Loading...</h1>
                </div>
            </PageWithNavbar>
        );
    }

    const isBiddingDisabled = auction.status === 'FINISHED' || auction.status === 'CANCELED';
    const biddingMessage = isBiddingDisabled
        ? auction.status === 'FINISHED'
            ? 'This auction has ended. Bidding is closed.'
            : 'This auction has been canceled. Bidding is closed.'
        : '';

    const highestBid = auction.bids.length > 0 ? auction.bids[0].amount : auction.startingPrice;

    return (
        <PageWithNavbar>
            <div className="container auction-detail mt-4">
                <div className="row justify-content-center">
                    <div className="col-md-6">
                        <div className="auction-gallery">
                            {auction.imageUrls.length > 0 && (
                                <div className="gallery-container">
                                    {auction.imageUrls.length > 1 && (
                                        <>
                                            <FaArrowLeft
                                                className="gallery-arrow"
                                                onClick={handlePrevImage}
                                            />
                                            <FaArrowRight
                                                className="gallery-arrow"
                                                onClick={handleNextImage}
                                            />
                                        </>
                                    )}
                                    <img
                                        src={`http://localhost:8080/${auction.imageUrls[currentImageIndex]}`}
                                        alt={`Auction item ${currentImageIndex + 1}`}
                                        className="img-fluid auction-image"
                                    />
                                    <div className="image-counter">
                                        Image {currentImageIndex + 1} of {auction.imageUrls.length}
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>
                    <div className="col-md-6">
                        <h2>{auction.itemName}</h2>
                        <p className="text-muted">
                            Seller: {auction.sellerName} (Joined: {new Date(auction.sellerSignupDate).toLocaleDateString()}) - Rating: {auction.sellerAverageRating.toFixed(1)}/5
                        </p>
                        <p>{auction.description}</p>
                        <p className="lead"><strong>Starting Price:</strong> ${auction.startingPrice.toFixed(2)}</p>
                        <p className="lead"><strong>Current Highest Bid:</strong> ${highestBid.toFixed(2)}</p>
                        <p><strong>Buy It Now Price:</strong> ${auction.buyItNowPrice.toFixed(2)}</p>
                        <p>
                            <strong>Reserve Price:</strong> {auction.reservePrice !== null ? `$${auction.reservePrice.toFixed(2)}` : 'No reserve price set'}
                        </p>
                        <p style={{ textTransform: 'capitalize' }}>
                            <strong>Status:</strong> {getStatusIcon(auction.status)} {auction.status.replace('_', ' ').toLowerCase()}
                        </p>
                        <p><strong>Start Date:</strong> {new Date(auction.startDate).toLocaleDateString()}</p>
                        <p><strong>End Date:</strong> {new Date(auction.endDate).toLocaleDateString()}</p>
                        
                        <div className={`bid-section ${isBiddingDisabled ? 'disabled' : ''}`}>
                            {isBiddingDisabled ? (
                                <p className="text-danger">{biddingMessage}</p>
                            ) : (
                                <>
                                    <h4>Place Your Bid</h4>
                                    <form onSubmit={handleBidSubmit}>
                                        <div className="form-group">
                                            <label>Bid Amount</label>
                                            <input
                                                type="number"
                                                className="form-control"
                                                min={highestBid + 1}
                                                value={bidAmount}
                                                onChange={(e) => setBidAmount(e.target.value)}
                                                placeholder={`Minimum $${(highestBid + 1).toFixed(2)}`}
                                                required
                                                disabled={isBiddingDisabled}
                                            />
                                        </div>
                                        <div className="form-check mb-3">
                                            <input
                                                className="form-check-input"
                                                type="checkbox"
                                                checked={agreementChecked}
                                                onChange={() => setAgreementChecked(!agreementChecked)}
                                                id="agreeCheckbox"
                                            />
                                            <label className="form-check-label" htmlFor="agreeCheckbox">
                                                I agree to be obligated to pay if I win this auction.
                                            </label>
                                        </div>
                                        <button type="submit" className="btn btn-primary" disabled={isBiddingDisabled}>Place Bid</button>
                                    </form>
                                </>
                            )}
                        </div>

                        <div className="bids-list mt-4">
                            <h4>Bid History</h4>
                            <div className="bid-history-container">
                                {auction.bids.length > 0 ? (
                                    auction.bids.map((bid, index) => (
                                        <div key={index} className="bid-entry">
                                            <strong>${bid.amount.toFixed(2)}</strong> by {bid.bidderUsername} at {new Date(bid.timestamp).toLocaleString()}
                                        </div>
                                    ))
                                ) : (
                                    <p>No bids have been placed yet.</p>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            {popupVisible && (
                <Popup
                    success={popupSuccess}
                    message={popupMessage}
                    onClose={handlePopupClose}
                />
            )}
        </PageWithNavbar>
    );
};

export default AuctionDetailPage;