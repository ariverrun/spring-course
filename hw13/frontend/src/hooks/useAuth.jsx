import { createContext, useContext, useEffect, useState } from 'react';

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [isAuthenticated, setIsAuthenticated] = useState(null);
  const [username, setUsername] = useState(null);
  const [loading, setLoading] = useState(true);

  const checkAuth = async () => {
    try {
      const response = await fetch('/api/v1/auth/me', {
        credentials: 'include'
      });
      
      if (response.ok) {
        const data = await response.json();
        setIsAuthenticated(data.authenticated);
        setUsername(data.username);
      } else {
        setIsAuthenticated(false);
        setUsername(null);
      }
    } catch (error) {
      setIsAuthenticated(false);
      setUsername(null);
    } finally {
      setLoading(false);
    }
  };

  const logout = async () => {
    await fetch('/api/v1/auth/logout', {
      method: 'POST',
      credentials: 'include'
    });
    setIsAuthenticated(false);
    setUsername(null);
  };

  useEffect(() => {
    checkAuth();
  }, []);

  return (
    <AuthContext.Provider value={{ isAuthenticated, username, loading, logout, checkAuth }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}