import React from 'react';
import './ProductCard.css';

const ProductCard = ({ producto, onAgregarCarrito, moneda = 'PEN' }) => {
  const precio = moneda === 'PEN' ? producto.precioPen : producto.precioUsd;
  const simbolo = moneda === 'PEN' ? 'S/' : '$';

  const handleAgregar = () => {
    onAgregarCarrito(producto, 1);
  };

  return (
    <div className="product-card">
      <div className="product-image">
        <img
          src={producto.imagen || '/placeholder.jpg'}
          onError={(e) => { e.target.src = 'https://via.placeholder.com/400'; }}
          alt={producto.nombre}
        />
        {producto.stock <= 5 && producto.stock > 0 && (
          <span className="badge-stock-bajo">¡Últimas unidades!</span>
        )}
        {producto.stock === 0 && (
          <span className="badge-sin-stock">Sin stock</span>
        )}
      </div>

      <div className="product-info">
        <h3 className="product-name">{producto.nombre}</h3>
        <p className="product-description">{producto.descripcion}</p>

        {producto.categoria && (
          <span className="product-category">📦 {producto.categoria}</span>
        )}

        <div className="product-footer">
          <div className="product-price">
            <span className="price-value">{simbolo} {precio.toFixed(2)}</span>
            <span className="stock-info">Stock: {producto.stock}</span>
          </div>

          <button
            className="btn-add-cart"
            onClick={handleAgregar}
            disabled={producto.stock === 0}
          >
            {producto.stock === 0 ? 'Sin stock' : '🛒 Agregar'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProductCard;