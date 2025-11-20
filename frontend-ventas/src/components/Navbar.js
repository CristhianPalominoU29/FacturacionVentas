import React from 'react';
import './Navbar.css';

const Navbar = ({ user, onLogout, carritoCount }) => {
  const handleLogout = () => {
    onLogout();
    window.location.hash = '#home';
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="navbar-brand">
          <a href="#home" style={{color: 'white', textDecoration: 'none'}}>
            <h1>🛒 Mi Tienda</h1>
          </a>
        </div>
        
        <div className="navbar-links">
          <a href="#home" className="nav-link">Inicio</a>
          
          {user ? (
            <>
              <a href="#carrito" className="nav-link carrito-link">
                🛒 Carrito
                {carritoCount > 0 && (
                  <span className="carrito-badge">{carritoCount}</span>
                )}
              </a>
              <span className="nav-user">👤 {user.nombre}</span>
              <button onClick={handleLogout} className="btn-logout">
                Cerrar Sesión
              </button>
            </>
          ) : (
            <>
              <a href="#login" className="nav-link">Iniciar Sesión</a>
              <a href="#register" className="btn-register">Registrarse</a>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;