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
          <a href="#home" className="brand-link">
            <span className="material-symbols-outlined brand-icon">storefront</span>
            <h1>Mi Tienda</h1>
          </a>
        </div>

        <div className="navbar-links">
          <a href="#home" className="nav-link">
            <span className="material-symbols-outlined">home</span>
            Inicio
          </a>

          {user ? (
            <>
              <a href="#carrito" className="nav-link carrito-link">
                <span className="material-symbols-outlined">shopping_cart</span>
                Carrito
                {carritoCount > 0 && (
                  <span className="carrito-badge">{carritoCount}</span>
                )}
              </a>

              <div className="nav-user">
                <span className="material-symbols-outlined">person</span>
                {user.nombre || user.email}
              </div>

              <button onClick={handleLogout} className="btn-logout">
                <span className="material-symbols-outlined">logout</span>
                Cerrar Sesión
              </button>
            </>
          ) : (
            <>
              <a href="#login" className="nav-link">
                <span className="material-symbols-outlined">login</span>
                Iniciar Sesión
              </a>
              <a href="#register" className="btn-register">
                <span className="material-symbols-outlined">person_add</span>
                Registrarse
              </a>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;