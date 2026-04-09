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
    const checkAuth = async () => {
      try {
        const currentUser = await apiClient.getCurrentUser();
        setUser(currentUser);
      } catch (error) {
        console.error('Failed to fetch current user:', error);
      } finally {
        setLoading(false);
      }
    };

    checkAuth();
  }, []);

  const login = async (login: string, motDePasse: string) => {
    const user = await apiClient.login(login, motDePasse);
    setUser(user);
  };

  const logout = async () => {
    await apiClient.logout();
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
