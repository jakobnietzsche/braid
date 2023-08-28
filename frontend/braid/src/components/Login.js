import React, { useState, useEffect } from 'react';
import axios from 'axios';

const LoginForm = ({ onLogin }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);

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
            console.log(response);
        } catch (error) {
            setError('Login failed. Please check your credentials and try again.');
        }
    };
    return (
        <div>
            <h2>Login</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Username: </label>
                    <input
                        type="text"
                        value={username}
                        onChange={e => setUsername(e.target.value)}
                    />
                </div>
                <div>
                    <label>Password: </label>
                    <input
                        type="password"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                    />
                </div>
                <div>
                    <input
                        type="checkbox"
                        id="stayLoggedIn"
                    />
                    <label htmlFor="stayLoggedIn">Stay Logged In</label>
                </div>
                {error && <div style={{color: 'red'}}>{error}</div>}
                <button type="submit">Login</button>
            </form>
        </div>
    );
}

function AuthenticatedView() {
    return <div>Welcome back! You are now logged in.</div>
}

function LoginPage() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);

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
        checkAuthentication().then(r => {});
    }, []);
    return (
        <div>
            {isLoggedIn ? <AuthenticatedView /> : <LoginForm onLogin={() => setIsLoggedIn(true)} />}
        </div>
    );
}

export default LoginPage;
