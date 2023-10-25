import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import React, { useState } from 'react';
import axios from 'axios';

const RegisterForm = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [error, setError] = useState(null);
    const [isRegistered, setIsRegistered] = useState(false);

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
            setIsRegistered(true);
        } catch (error) {
            setError('Registration failed. Please check your details and try again.');
        }
    };

    if (isRegistered) {
        return (
            <div>
                <h2>Registration Success</h2>
                <p>Thanks for registering! You can now <a href="/login">login</a> using your credentials.</p>
            </div>
        );
    } else {
        return (
            <div>
                <h2>Register</h2>
                <form onSubmit={handleSubmit}>
                    <div>
                        <TextField
                            type="email"
                            value={email}
                            onChange={e => setEmail(e.target.value)}
                            label="Email"
                            variant="outlined"
                            className="authTextField"
                        />
                    </div>
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
                            type="password"
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            label="Password"
                            variant="outlined"
                            className="authTextField"
                        />
                    </div>
                    {error && <div style={{color: 'red'}}>{error}</div>}
                    <Button
                        type="submit"
                        variant="contained"
                        className="authButton">
                        Register
                    </Button>
                </form>
            </div>
        );
    }
};

export default RegisterForm;