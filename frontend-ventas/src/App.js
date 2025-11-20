import React, { useEffect, useState } from 'react';
import { useAuth } from './hooks/useAuth';
import { useCarrito } from './hooks/useCarrito';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Carrito from './pages/Carrito';
import Checkout from './pages/Checkout';
import './App.css';

function App() {
  const { user, loginUser, logoutUser } = useAuth();
  const {
    carrito,
    agregarAlCarrito,
    eliminarDelCarrito,
    actualizarCantidad,
    vaciarCarrito,
    calcularTotal,
    cantidadTotal
  } = useCarrito();

  const [currentPage, setCurrentPage] = useState('home');

  // Manejar la navegación
  useEffect(() => {
    const handleNavigation = () => {
      const hash = window.location.hash.slice(1) || 'home';
      setCurrentPage(hash);
    };

    handleNavigation();
    window.addEventListener('hashchange', handleNavigation);
    
    return () => window.removeEventListener('hashchange', handleNavigation);
  }, []);

  const handleAgregarCarrito = (producto, cantidad) => {
    agregarAlCarrito(producto, cantidad);
    alert(`✅ ${producto.nombre} agregado al carrito`);
  };

  const renderPage = () => {
    switch (currentPage) {
      case 'login':
        return <Login onLogin={loginUser} />;
      
      case 'register':
        return <Register onLogin={loginUser} />;
      
      case 'carrito':
        return (
          <Carrito
            carrito={carrito}
            onActualizarCantidad={actualizarCantidad}
            onEliminar={eliminarDelCarrito}
            onVaciar={vaciarCarrito}
            calcularTotal={calcularTotal}
          />
        );
      
      case 'checkout':
        if (!user) {
          window.location.hash = '#login';
          return null;
        }
        return (
          <Checkout
            user={user}
            carrito={carrito}
            vaciarCarrito={vaciarCarrito}
            calcularTotal={calcularTotal}
          />
        );
      
      default:
        return (
          <Home
            onAgregarCarrito={handleAgregarCarrito}
            user={user}
          />
        );
    }
  };

  return (
    <div className="App">
      <Navbar
        user={user}
        onLogout={logoutUser}
        carritoCount={cantidadTotal}
      />
      
      <main className="main-content">
        {renderPage()}
      </main>

      <footer className="footer">
        <p>© 2024 Mi Tienda - Sistema de Ventas con Comprobantes Electrónicos</p>
      </footer>
    </div>
  );
}

export default App;