import React, { useState } from 'react';
import axios from 'axios';

const RegisterForm = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [error, setError] = useState(null);

    const handleSubmit = async (event) => {
        event.preventDefault();

        try {
            const response = await axios.post('https://localhost:8080/users/register', {
                username,
                password,
                email,
            }, {
                withCredentials: true
            });
            console.log(response);
        } catch (error) {
            setError('Registration failed. Please check your details and try again.');
        }
    };

    return (
        <div>
            <h2>Register</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Email: </label>
                    <input
                        type="email"
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                    />
                </div>
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
                {error && <div style={{color: 'red'}}>{error}</div>}
                <button type="submit">Register</button>
            </form>
        </div>
    );
};

export default RegisterForm;