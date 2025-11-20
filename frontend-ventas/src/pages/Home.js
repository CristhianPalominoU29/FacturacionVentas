import React, { useState, useEffect } from 'react';
import ProductCard from '../components/ProductCard';
import { getProductos } from '../services/api';
import './Home.css';

const Home = ({ onAgregarCarrito, user }) => {
  const [productos, setProductos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [moneda, setMoneda] = useState('PEN');
  const [busqueda, setBusqueda] = useState('');
  const [categoriaFiltro, setCategoriaFiltro] = useState('todas');

  useEffect(() => {
    cargarProductos();
  }, []);

  const cargarProductos = async () => {
    try {
      setLoading(true);
      setError('');
      const response = await getProductos();

      // Ahora el backend devuelve directamente el array
      if (Array.isArray(response)) {
        setProductos(response);
      } else if (response?.data) {
        setProductos(response.data);
      } else {
        throw new Error("Respuesta inválida");
      }
    } catch (err) {
      console.error("Error cargando productos:", err);
      setError('No se pudieron cargar los productos');
    } finally {
      setLoading(false);
    }
  };

  const categorias = ['todas', ...new Set(productos.map(p => p.categoria).filter(Boolean))];

  const productosFiltrados = productos.filter(producto => {
    const matchBusqueda = producto.nombre.toLowerCase().includes(busqueda.toLowerCase()) ||
                         producto.descripcion?.toLowerCase().includes(busqueda.toLowerCase());
    const matchCategoria = categoriaFiltro === 'todas' || producto.categoria === categoriaFiltro;
    return matchBusqueda && matchCategoria;
  });

  const handleAgregarCarrito = (producto, cantidad = 1) => {
    if (!user) {
      alert('Debes iniciar sesión para agregar al carrito');
      window.location.href = '/login';
      return;
    }
    onAgregarCarrito(producto, cantidad);
  };

  if (loading) return <div className="loading-container"><div className="spinner"></div><p>Cargando productos...</p></div>;
  if (error) return <div className="error-container"><p className="error-text">{error}</p><button onClick={cargarProductos} className="btn-retry">Reintentar</button></div>;

  return (
    <div className="home-container">
      <div className="home-header">
        <h1>Nuestros Productos</h1>
        <p className="subtitle">Encuentra todo lo que necesitas</p>
      </div>

      <div className="filters-section">
        <input type="text" placeholder="Buscar productos..." value={busqueda} onChange={e => setBusqueda(e.target.value)} className="search-input" />
        <select value={categoriaFiltro} onChange={e => setCategoriaFiltro(e.target.value)} className="filter-select">
          {categorias.map(cat => <option key={cat} value={cat}>{cat === 'todas' ? 'Todas las categorías' : cat}</option>)}
        </select>
        <select value={moneda} onChange={e => setMoneda(e.target.value)} className="filter-select">
          <option value="PEN">Soles (S/)</option>
          <option value="USD">Dólares ($)</option>
        </select>
      </div>

      <div className="results-info">
        <p>{productosFiltrados.length} producto(s) encontrado(s)</p>
      </div>

      {productosFiltrados.length === 0 ? (
        <div className="no-products"><p>No se encontraron productos</p></div>
      ) : (
        <div className="products-grid">
          {productosFiltrados.map(producto => (
            <ProductCard
              key={producto.id}
              producto={producto}
              onAgregarCarrito={handleAgregarCarrito}
              moneda={moneda}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default Home;