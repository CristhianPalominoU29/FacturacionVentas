import React, { useState } from 'react';
import './Carrito.css';

const Carrito = ({ carrito, onActualizarCantidad, onEliminar, onVaciar, calcularTotal }) => {
  const [moneda, setMoneda] = useState('PEN');
  
  const total = calcularTotal(moneda);
  const simbolo = moneda === 'PEN' ? 'S/' : '$';

  const handleCantidadChange = (productoId, nuevaCantidad) => {
    const cantidad = parseInt(nuevaCantidad);
    if (cantidad > 0) {
      onActualizarCantidad(productoId, cantidad);
    }
  };

  const handleProcederCheckout = () => {
    if (carrito.length === 0) {
      alert('El carrito está vacío');
      return;
    }
    window.location.hash = '#checkout';
  };

  if (carrito.length === 0) {
    return (
      <div className="carrito-vacio">
        <div className="carrito-vacio-content">
          <h2>Tu carrito está vacío</h2>
          <p>Agrega productos para comenzar tu compra</p>
          <a href="#home" className="btn-volver">
            Ver productos
          </a>
        </div>
      </div>
    );
  }

  return (
    <div className="carrito-container">
      <div className="carrito-header">
        <h1>🛒 Mi Carrito</h1>
        <button onClick={onVaciar} className="btn-vaciar">
          Vaciar carrito
        </button>
      </div>

      {/* Selector de moneda */}
      <div className="moneda-selector">
        <label>Ver precios en:</label>
        <select value={moneda} onChange={(e) => setMoneda(e.target.value)}>
          <option value="PEN">Soles (S/)</option>
          <option value="USD">Dólares ($)</option>
        </select>
      </div>

      {/* Lista de productos en el carrito */}
      <div className="carrito-items">
        {carrito.map(item => {
          const precio = moneda === 'PEN' ? item.precioPen : item.precioUsd;
          const subtotal = precio * item.cantidad;

          return (
            <div key={item.id} className="carrito-item">
              <div className="item-imagen">
                <img 
                  src={item.imagen || 'https://via.placeholder.com/100'} 
                  alt={item.nombre}
                />
              </div>

              <div className="item-info">
                <h3>{item.nombre}</h3>
                <p className="item-descripcion">{item.descripcion}</p>
                {item.categoria && (
                  <span className="item-categoria">📦 {item.categoria}</span>
                )}
              </div>

              <div className="item-precio">
                <span className="precio-unitario">{simbolo} {precio.toFixed(2)}</span>
                <span className="precio-label">c/u</span>
              </div>

              <div className="item-cantidad">
                <label>Cantidad:</label>
                <input
                  type="number"
                  min="1"
                  max={item.stock}
                  value={item.cantidad}
                  onChange={(e) => handleCantidadChange(item.id, e.target.value)}
                />
                <span className="stock-disponible">Stock: {item.stock}</span>
              </div>

              <div className="item-subtotal">
                <span className="subtotal-label">Subtotal:</span>
                <span className="subtotal-valor">{simbolo} {subtotal.toFixed(2)}</span>
              </div>

              <button 
                className="btn-eliminar"
                onClick={() => onEliminar(item.id)}
                title="Eliminar producto"
              >
                🗑️
              </button>
            </div>
          );
        })}
      </div>

      {/* Resumen del carrito */}
      <div className="carrito-resumen">
        <div className="resumen-content">
          <h3>Resumen de compra</h3>
          
          <div className="resumen-linea">
            <span>Total de productos:</span>
            <span>{carrito.length}</span>
          </div>

          <div className="resumen-linea">
            <span>Cantidad total:</span>
            <span>{carrito.reduce((sum, item) => sum + item.cantidad, 0)} unidades</span>
          </div>

          <div className="resumen-total">
            <span>Total a pagar:</span>
            <span className="total-valor">{simbolo} {total.toFixed(2)}</span>
          </div>

          <button className="btn-checkout" onClick={handleProcederCheckout}>
            Proceder al pago
          </button>

          <a href="/" className="btn-continuar">
            Continuar comprando
          </a>
        </div>
      </div>
    </div>
  );
};

export default Carrito;