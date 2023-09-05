import React, { useState } from 'react';
import Register from './Register';
import Login from './Login';

const Auth = () => {
    const [isRegistering, setIsRegistering] = useState(false);
    const [isLoggedIn, setIsLoggedIn] = useState(false);

    return (
      <div>
          {isRegistering
              ? <Register />
              : <Login isLoggedIn={isLoggedIn} setIsLoggedIn={setIsLoggedIn} />}
          {!isLoggedIn && (
              <button onClick={() => setIsRegistering(!isRegistering)}>
                {isRegistering ? 'Go to Login' : 'Go to Register'}
              </button>
              )}
      </div>
    );
};

export default Auth;
