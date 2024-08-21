import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import PageWithNavbar from '../components/PageWithNavbar';
import { FaCircle, FaArrowLeft, FaArrowRight } from 'react-icons/fa';
import './AuctionDetailPage.css';

const AuctionDetailPage = () => {
    const { id } = useParams();
    const [auction, setAuction] = useState(null);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);

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
                        <p className="lead">Starting Price: ${auction.startingPrice.toFixed(2)}</p>
                        <p>Buy It Now Price: ${auction.buyItNowPrice.toFixed(2)}</p>
                        <p>
                            Reserve Price: {auction.reservePrice !== null ? `$${auction.reservePrice.toFixed(2)}` : 'No reserve price set'}
                        </p>
                        <p style={{ textTransform: 'capitalize' }}>
                            Status: {getStatusIcon(auction.status)} {auction.status.replace('_', ' ').toLowerCase()}
                        </p>
                        <p>Start Date: {new Date(auction.startDate).toLocaleDateString()}</p>
                        <p>End Date: {new Date(auction.endDate).toLocaleDateString()}</p>
                        
                        {}
                        <div className={`bid-section ${isBiddingDisabled ? 'disabled' : ''}`}>
                            {isBiddingDisabled ? (
                                <p className="text-danger">{biddingMessage}</p>
                            ) : (
                                <h4>Place Your Bid</h4>
                            )}
                            <form>
                                <div className="form-group">
                                    <label>Bid Amount</label>
                                    <input type="number" className="form-control" min={auction.startingPrice} placeholder={`Minimum $${auction.startingPrice}`} required disabled={isBiddingDisabled} />
                                </div>
                                <button type="submit" className="btn btn-primary" disabled={isBiddingDisabled}>Place Bid</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </PageWithNavbar>
    );
};

export default AuctionDetailPage;
