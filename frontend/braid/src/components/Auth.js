import Button from '@mui/material/Button';
import React, { useState } from 'react';
import Register from './Register';
import Login from './Login';
import '../styles/styles.css';

const Auth = () => {
    const [isRegistering, setIsRegistering] = useState(false);
    const [isLoggedIn, setIsLoggedIn] = useState(false);

    return (
      <div>
          {isRegistering
              ? <Register />
              : <Login isLoggedIn={isLoggedIn} setIsLoggedIn={setIsLoggedIn} />}
          {!isLoggedIn && (
              <Button onClick={() => setIsRegistering(!isRegistering)}
                      variant="contained"
                      className="authButton">
                {isRegistering ? 'Go to Login' : 'Go to Register'}
              </Button>
              )}
      </div>
    );
};

export default Auth;
