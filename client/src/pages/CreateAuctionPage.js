import React, { useContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { Formik, Form, Field, ErrorMessage } from 'formik';
import * as Yup from 'yup';
import { AuthContext } from '../context/AuthContext';
import PageWithNavbar from '../components/PageWithNavbar';
import { FaPlusCircle } from 'react-icons/fa';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import './CreateAuctionPage.css';

const CreateAuctionPage = () => {
    const navigate = useNavigate();
    const { user } = useContext(AuthContext);
    const [serverError, setServerError] = useState('');

    const initialValues = {
        itemName: '',
        description: '',
        startingPrice: '',
        endDate: '',
        buyItNowPrice: '',
        reservePrice: '',
        images: []
    };

    const validationSchema = Yup.object().shape({
        itemName: Yup.string().required('Item Name is required'),
        description: Yup.string().required('Description is required'),
        startingPrice: Yup.number().required('Starting Price is required').min(0, 'Starting Price cannot be negative'),
        endDate: Yup.date()
            .required('End Date is required')
            .min(new Date(), 'End Date must be greater than the current date'),
        buyItNowPrice: Yup.number().min(Yup.ref('startingPrice'), 'Buy It Now Price must be greater than Starting Price').notRequired(),
        reservePrice: Yup.number().min(Yup.ref('startingPrice'), 'Reserve Price must be greater than or equal to Starting Price').notRequired(),
        images: Yup.array().of(Yup.mixed()).notRequired()
    });

    const handleSubmit = async (values) => {
        const formData = new FormData();
        formData.append('itemName', values.itemName);
        formData.append('description', values.description);
        formData.append('startingPrice', values.startingPrice);
        
        const formatDate = (date) => {
            return date.toISOString().slice(0, 19).replace('T', ' ');
        };
        
        formData.append('endDate', formatDate(values.endDate));
        
        if (values.buyItNowPrice) formData.append('buyItNowPrice', values.buyItNowPrice);
        if (values.reservePrice) formData.append('reservePrice', values.reservePrice);
        values.images.forEach((image) => {
            formData.append('images', image);
        });

        const token = localStorage.getItem('token');

        try {
            await axios.post('http://localhost:8080/api/auctions/create', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    Authorization: `Bearer ${token}`,
                },
            });
            navigate('/dashboard');
        } catch (error) {
            if (error.response && error.response.status === 400) {
                setServerError(error.response.data);
            } else {
                setServerError('An unexpected error occurred. Please try again later.');
            }
        }
    };

    return (
        <>
            <PageWithNavbar>
            <div className="create-auction-container with-navbar">
                <div className="form-container">
                    <h1>Create Auction</h1>
                    <Formik
                        initialValues={initialValues}
                        validationSchema={validationSchema}
                        onSubmit={handleSubmit}
                    >
                        {({ setFieldValue, values }) => (
                            <Form>
                                <div className="form-group">
                                    <label htmlFor="itemName" className="form-label">Item Name</label>
                                    <Field type="text" className="form-control" id="itemName" name="itemName" />
                                    <ErrorMessage name="itemName" component="div" className="text-danger" />
                                </div>

                                <div className="form-group">
                                    <label htmlFor="description" className="form-label">Description</label>
                                    <Field as="textarea" className="form-control description-field" id="description" name="description" />
                                    <ErrorMessage name="description" component="div" className="text-danger" />
                                </div>

                                <div className="form-group">
                                    <label htmlFor="startingPrice" className="form-label">Starting Price</label>
                                    <Field type="number" className="form-control" id="startingPrice" name="startingPrice" />
                                    <ErrorMessage name="startingPrice" component="div" className="text-danger" />
                                </div>

                                <div className="form-group">
                                    <label htmlFor="endDate" className="form-label">End Date</label>
                                    <DatePicker
                                        selected={values.endDate}
                                        onChange={date => setFieldValue('endDate', date)}
                                        className="form-control"
                                        showTimeSelect
                                        timeFormat="HH:mm"
                                        timeIntervals={15}
                                        dateFormat="MMMM d, yyyy h:mm aa"
                                        id="endDate"
                                        minDate={new Date()}
                                    />
                                    <ErrorMessage name="endDate" component="div" className="text-danger" />
                                </div>

                                <div className="form-group">
                                    <label htmlFor="buyItNowPrice" className="form-label">Buy It Now Price</label>
                                    <Field type="number" className="form-control" id="buyItNowPrice" name="buyItNowPrice" />
                                    <ErrorMessage name="buyItNowPrice" component="div" className="text-danger" />
                                </div>

                                <div className="form-group">
                                    <label htmlFor="reservePrice" className="form-label">Reserve Price</label>
                                    <Field type="number" className="form-control" id="reservePrice" name="reservePrice" />
                                    <ErrorMessage name="reservePrice" component="div" className="text-danger" />
                                </div>

                                <div className="form-group">
                                    <label htmlFor="images" className="form-label">Images</label>
                                    <input type="file" className="form-control" id="images" multiple onChange={(event) => {
                                        setFieldValue('images', Array.from(event.currentTarget.files));
                                    }} />
                                    <ErrorMessage name="images" component="div" className="text-danger" />
                                </div>

                                {serverError && <div className="alert alert-danger">{serverError}</div>}
                                
                                <button type="submit" className="btn btn-primary">
                                    <FaPlusCircle className="me-2" /><span>Create Auction</span>
                                </button>
                            </Form>
                        )}
                    </Formik>
                </div>
            </div>

    </PageWithNavbar>
        </>
        
    );
};

export default CreateAuctionPage;