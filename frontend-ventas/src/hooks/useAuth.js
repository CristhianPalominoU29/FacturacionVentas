import { useLocalStorage } from './useLocalStorage';

export const useAuth = () => {
  const [user, setUser] = useLocalStorage('user', null);

  const loginUser = (userData) => {
    setUser(userData);
  };

  const logoutUser = () => {
    setUser(null);
    localStorage.removeItem('carrito');
  };

  const isAuthenticated = () => {
    return user !== null;
  };

  return {
    user,
    loginUser,
    logoutUser,
    isAuthenticated
  };
};