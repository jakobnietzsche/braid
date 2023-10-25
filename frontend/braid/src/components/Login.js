import React, { useState, useEffect } from 'react';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import axios from 'axios';
import '../styles/styles.css';

const LogoutButton = ({ onLogout }) => (
    <button onClick={onLogout}>Logout</button>
);

const LoginForm = ({ onLogin }) => {
    const [isPasswordVisible, setIsPasswordVisible] = useState(false);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);

    const togglePasswordVisibility = () => {
        setIsPasswordVisible(!isPasswordVisible);
    }

    const handleSubmit = async (event) => {
        event.preventDefault();
        const stayLoggedIn = document.getElementById("stayLoggedIn").checked;

        try {
            const response = await axios.post('https://localhost:8080/accounts/login', {
                username,
                password,
                stayLoggedIn
            }, {
                withCredentials: true
            });
            if (response.status === 200) {
                onLogin();
            }
        } catch (error) {
            setError('Login failed. Please check your credentials and try again.');
        }
    };
    return (
        <div>
            <h2>Login</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <TextField
                        type="text"
                        value={username}
                        onChange={e => setUsername(e.target.value)}
                        label="Username"
                        variant="outlined"
                        className="authTextField"
                    />
                </div>
                <div>
                    <TextField
                        type={isPasswordVisible ? 'text' : 'password'}
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        label="Password"
                        variant="outlined"
                        className="authTextField"
                    />
                    <Button
                        type="button"
                        variant="contained"
                        className="authButton"
                        onClick={togglePasswordVisibility}
                    >
                        {isPasswordVisible ? 'Hide' : 'Show'}
                    </Button>
                </div>
                <div>
                    <input
                        type="checkbox"
                        id="stayLoggedIn"
                    />
                    <label htmlFor="stayLoggedIn">Remember me</label>
                </div>
                {error && <div style={{color: 'red'}}>{error}</div>}
                <Button
                    type="submit"
                    variant="contained"
                    className="authButton"
                > Login
                </Button>
            </form>
        </div>
    );
}

function AuthenticatedView({ onLogout }) {
    return (
        <div>
            Welcome back! You are now logged in.
            <LogoutButton onLogout={onLogout} />
        </div>
    );
}

function LoginPage({isLoggedIn, setIsLoggedIn }) {

    const handleLogout = async () => {
        try {
            const response = await axios.post('https://localhost:8080/accounts/logout', {}, {
                withCredentials: true
            });

            if (response.status === 200) {
                setIsLoggedIn(false);
            }
        } catch (error) {
            console.error('Failed to logout', error);
        }
    };

    const checkAuthentication = async () => {
        try {
            const response = await axios.get('https://localhost:8080/accounts/check-auth',
                {
                    withCredentials: true
                });
            console.log(response);
            if (response.data && response.data.isAuthenticated) {
                setIsLoggedIn(true);
            }
        } catch (error) {
            console.error('Failed to check authentication status', error);
        }
    };

    useEffect(() => {
        checkAuthentication().then();
    }, []);
    return (
        <div>
            {isLoggedIn
                ? <AuthenticatedView onLogout={handleLogout} />
                : <LoginForm onLogin={() => setIsLoggedIn(true)} />}
        </div>
    );
}

export default LoginPage;
