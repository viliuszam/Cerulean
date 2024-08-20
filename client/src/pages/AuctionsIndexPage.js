import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import PageWithNavbar from '../components/PageWithNavbar';
import { FaCircle, FaArrowLeft, FaArrowRight } from 'react-icons/fa';

const AuctionsIndexPage = () => {
    const [auctions, setAuctions] = useState([]);
    const [totalPages, setTotalPages] = useState(1);
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalElements, setTotalElements] = useState(0);
    const [filters, setFilters] = useState({
        itemName: '',
        status: '',
        sellerName: ''
    });

    const navigate = useNavigate();

    useEffect(() => {
        fetchAuctions();
    }, [currentPage, pageSize, filters]);

    const fetchAuctions = async () => {
        try {
            const token = localStorage.getItem('token');

            const response = await axios.get('http://localhost:8080/api/auctions', {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                params: {
                    page: currentPage,
                    size: pageSize,
                    itemName: filters.itemName,
                    status: filters.status,
                    username: filters.sellerName
                }
            });
            setAuctions(response.data.content);
            setTotalPages(response.data.totalPages);
            setTotalElements(response.data.totalElements);
        } catch (error) {
            console.error('Error fetching auctions:', error);
        }
    };

    const handleFilterChange = (e) => {
        setFilters({
            ...filters,
            [e.target.name]: e.target.value
        });
    };

    const handlePageSizeChange = (e) => {
        setPageSize(e.target.value);
        setCurrentPage(0);
    };

    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    const handleRowClick = (auctionId) => {
        navigate(`/auction/${auctionId}`);
    };

    const truncateDescription = (description, maxLength = 50) => {
        if (description.length > maxLength) {
            return description.substring(0, maxLength) + '...';
        }
        return description;
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

    return (
        <PageWithNavbar>
            <div className="create-auction-container with-navbar">
                <div className="container mt-4" style={{ maxWidth: '80%', margin: '0 auto', textAlign: 'center' }}>
                    <h1 className="mb-4">Auctions</h1>

                    <div className="d-flex justify-content-center align-items-center mb-4 flex-wrap gap-3">
                        <div className="d-flex align-items-center">
                            <label htmlFor="itemName" className="me-2">Item Name:</label>
                            <input
                                id="itemName"
                                type="text"
                                name="itemName"
                                value={filters.itemName}
                                onChange={handleFilterChange}
                                placeholder="Filter by Item Name"
                                className="form-control form-control-sm"
                                style={{ width: '150px' }}
                            />
                            <label htmlFor="sellerName" className="me-2">Seller Name:</label>
                            <input
                                id="sellerName"
                                type="text"
                                name="sellerName"
                                value={filters.sellerName}
                                onChange={handleFilterChange}
                                placeholder="Filter by Seller Name"
                                className="form-control form-control-sm"
                                style={{ width: '150px' }}
                            />
                        </div>
                        <div className="d-flex align-items-center">
                            <label htmlFor="status" className="me-2">Status:</label>
                            <select
                                id="status"
                                name="status"
                                value={filters.status}
                                onChange={handleFilterChange}
                                className="form-select form-select-sm"
                                style={{ width: '150px' }}
                            >
                                <option value="">All Statuses</option>
                                <option value="IN_PROGRESS">In Progress</option>
                                <option value="FINISHED">Finished</option>
                                <option value="CANCELED">Canceled</option>
                            </select>
                            <label className="me-2">Items per page:</label>
                            <select
                                value={pageSize}
                                onChange={handlePageSizeChange}
                                className="form-select form-select-sm"
                                style={{ width: '100px' }}
                            >
                                <option value={5}>5</option>
                                <option value={10}>10</option>
                                <option value={20}>20</option>
                                <option value={50}>50</option>
                            </select>
                        </div>
                    </div>

                    <div className="table-responsive">
                    <table className="table table-hover" style={{ border: '1px solid #ddd', borderCollapse: 'collapse' }}>
                            <thead style={{ borderBottom: '2px solid #ddd', textAlign: 'center' }}>
                                <tr>
                                    <th style={{ paddingRight: '10px', borderRight: '2px solid #ddd' }}>Item</th>
                                    <th style={{ paddingRight: '10px', borderRight: '2px solid #ddd' }}>Description</th>
                                    <th style={{ paddingRight: '10px', borderRight: '2px solid #ddd' }}>Starting Price</th>
                                    <th style={{ paddingRight: '10px', borderRight: '2px solid #ddd' }}>Buy It Now Price</th>
                                    <th style={{ paddingRight: '10px', borderRight: '2px solid #ddd' }}>Seller</th>
                                    <th style={{ paddingRight: '10px', borderRight: '2px solid #ddd' }}>Status</th>
                                    <th style={{ paddingRight: '10px', borderRight: '2px solid #ddd' }}>Start Date</th>
                                    <th style={{ paddingRight: '10px' }}>End Date</th>
                                </tr>
                            </thead>
                                <tbody>
                                {auctions.map((auction) => (
                                    <tr 
                                        key={auction.id} 
                                        onClick={() => handleRowClick(auction.id)} 
                                        style={{ cursor: 'pointer', borderBottom: '1px solid #ddd' }}
                                    >
                                        <td style={{ textAlign: 'center', padding: '10px 0' }}>
                                            <div className="d-flex flex-column align-items-center">
                                                <img
                                                    src={auction.imageUrls.length > 0
                                                        ? `http://localhost:8080/${auction.imageUrls[0]}`
                                                        : 'http://localhost:8080/uploads/unknown.png'}
                                                    alt={auction.itemName}
                                                    style={{ width: '100px', height: 'auto', marginLeft: '5px', borderRadius: '5px' }}
                                                />
                                                <br></br>
                                                <span style={{ textAlign: 'center' }}>{auction.itemName}</span>
                                            </div>
                                        </td>
                                        <td style={{ padding: '10px' }}>{truncateDescription(auction.description)}</td>
                                        <td style={{ padding: '10px' }}>€{auction.startingPrice.toFixed(2)}</td>
                                        <td style={{ padding: '10px' }}>€{auction.buyItNowPrice.toFixed(2)}</td>
                                        <td style={{ padding: '10px' }}>{auction.sellerName}</td>
                                        <td style={{ padding: '10px', textTransform: 'capitalize' }}>
                                            {getStatusIcon(auction.status)} {auction.status.replace('_', ' ').toLowerCase()}
                                        </td>
                                        <td style={{ padding: '10px' }}>{new Date(auction.startDate).toLocaleDateString()}</td>
                                        <td style={{ padding: '10px' }}>{new Date(auction.endDate).toLocaleDateString()}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>

                    <div className="d-flex justify-content-center mt-4">
                        <div>
                            Showing {currentPage * pageSize + 1} to {Math.min((currentPage + 1) * pageSize, totalElements)} of {totalElements} entries
                        </div>
                    </div>

                    <div className="d-flex justify-content-center mt-3">
                        <button
                            disabled={currentPage === 0}
                            onClick={() => handlePageChange(currentPage - 1)}
                            className="btn btn-secondary me-2"
                        >
                            <FaArrowLeft />
                        </button>
                        <button
                            disabled={currentPage + 1 >= totalPages}
                            onClick={() => handlePageChange(currentPage + 1)}
                            className="btn btn-secondary"
                        >
                            <FaArrowRight />
                        </button>
                    </div>
                </div>
            </div>
        </PageWithNavbar>
    );
};

export default AuctionsIndexPage;
