'use client';

import React, { createContext, useContext, useState, useEffect } from 'react';
import { apiClient, User } from './api-client';

interface AuthContextType {
  user: User | null;
  loading: boolean;
  login: (login: string, motDePasse: string) => Promise<void>;
  logout: () => Promise<void>;
  isAdmin: () => boolean;
  isMedecin: () => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // CRUCIAL : Utiliser uniquement le localStorage, pas d'appel API
    const loadUserFromStorage = () => {
      try {
        const token = localStorage.getItem('token');
        const storedUser = localStorage.getItem('user');
        
        console.log('Loading user from storage - Token:', !!token, 'User:', !!storedUser);
        
        if (token && storedUser) {
          const parsedUser = JSON.parse(storedUser);
          console.log('User loaded from storage:', parsedUser);
          setUser(parsedUser);
        } else {
          console.log('No user found in storage');
          setUser(null);
        }
      } catch (error) {
        console.error('Error loading user from storage:', error);
        setUser(null);
      } finally {
        setLoading(false);
      }
    };
    
    loadUserFromStorage();
  }, []);

  const login = async (login: string, motDePasse: string) => {
    try {
      const userData = await apiClient.login(login, motDePasse);
      console.log('Login successful:', userData);
      
      // Stocker dans localStorage
      localStorage.setItem('token', userData.token || 'dummy-token');
      localStorage.setItem('user', JSON.stringify(userData));
      
      setUser(userData);
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  };

  const logout = async () => {
    try {
      await apiClient.logout();
    } catch (error) {
      console.error('Logout error:', error);
    }
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  const isAdmin = () => user?.role === 'ADMIN';
  const isMedecin = () => user?.role === 'MEDECIN';

  return (
    <AuthContext.Provider value={{ user, loading, login, logout, isAdmin, isMedecin }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}