import React, { useContext } from 'react';
import { Formik, Form, Field, ErrorMessage } from 'formik';
import * as Yup from 'yup';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import { Icon } from '@iconify/react';
import userIcon from '@iconify/icons-mdi/account';
import lockIcon from '@iconify/icons-mdi/lock';
import heartIcon from '@iconify/icons-mdi/heart';
import cartIcon from '@iconify/icons-mdi/cart';
import { AuthContext } from '../context/AuthContext';

const Container = styled.div`
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background-color: #f0f8ff;
`;

const FormWrapper = styled.div`
    background: white;
    padding: 2rem;
    border-radius: 8px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    width: 350px;
    text-align: center;
`;

const Title = styled.h1`
    color: #007acc;
    margin-bottom: 1rem;
    display: flex;
    align-items: center;
    justify-content: center;

    & > svg {
        margin-left: 0.5rem;
    }
`;

const InputField = styled.div`
    margin-bottom: 1rem;
    display: flex;
    align-items: center;
    background-color: #f0f8ff;
    border-radius: 5px;
    padding: 0.5rem;

    & > svg {
        margin-right: 0.5rem;
    }
`;

const StyledField = styled(Field)`
    border: none;
    outline: none;
    width: 100%;
    background: none;
`;

const ErrorText = styled.div`
    color: red;
    font-size: 0.8rem;
    margin-top: 0.5rem;
`;

const StyledButton = styled.button`
    width: 100%;
    padding: 0.75rem;
    border: none;
    border-radius: 5px;
    background-color: #007acc;
    color: white;
    font-size: 1rem;
    cursor: pointer;
    transition: background-color 0.3s;

    &:hover {
        background-color: #005fa3;
    }

    &:disabled {
        background-color: #b0c4de;
        cursor: not-allowed;
    }
`;

const LoginPage = () => {
    const { login } = useContext(AuthContext);
    const navigate = useNavigate();

    const initialValues = {
        username: '',
        password: ''
    };

    const validationSchema = Yup.object({
        username: Yup.string().required('Username is required'),
        password: Yup.string().required('Password is required')
    });

    const onSubmit = async (values, { setSubmitting, setStatus }) => {
        try {
            const response = await axios.post('http://localhost:8080/api/auth/login', values);
            const token = response.data.token;
            const userResponse = await axios.get('http://localhost:8080/api/auth/user', {
                headers: { Authorization: `Bearer ${token}` }
            });
            login(token, userResponse.data);
            navigate('/dashboard');
        } catch (error) {
            if (error.response && error.response.data) {
                setStatus({ error: error.response.data });
            } else {
                setStatus({ error: { message: 'An unknown error occurred' } });
            }
        }
        setSubmitting(false);
    };

    return (
        <Container>
            <FormWrapper>
                <Title>
                    Log in to Cerulean <Icon icon={heartIcon} color="#007acc" width="24" height="24" /> <Icon icon={cartIcon} color="#007acc" width="24" height="24" />
                </Title>
                <Formik
                    initialValues={initialValues}
                    validationSchema={validationSchema}
                    onSubmit={onSubmit}
                    validateOnBlur={false}
                    validateOnChange={false}
                >
                    {({ isSubmitting, status }) => (
                        <Form>
                            <InputField>
                                <Icon icon={userIcon} width="20" height="20" />
                                <StyledField type="text" name="username" placeholder="Username" />
                            </InputField>
                            <ErrorMessage name="username" component={ErrorText} />
                            <InputField>
                                <Icon icon={lockIcon} width="20" height="20" />
                                <StyledField type="password" name="password" placeholder="Password" />
                            </InputField>
                            <ErrorMessage name="password" component={ErrorText} />
                            <StyledButton type="submit" disabled={isSubmitting}>
                                {isSubmitting ? 'Logging in...' : 'Log In'}
                            </StyledButton>
                            {status && status.error && <ErrorText>{status.error.message}</ErrorText>}
                        </Form>
                    )}
                </Formik>
                <p>
                    Don't have an account? <Link to="/signup">Sign up here</Link>
                </p>
            </FormWrapper>
        </Container>
    );
};

export default LoginPage;
