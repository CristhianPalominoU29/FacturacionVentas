import React, { useState } from 'react';
import { login } from '../services/api';
import './Auth.css';

const Login = ({ onLogin }) => {
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError(''); // Limpiar error al escribir
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    console.log('Intentando login con:', formData); // Debug

    try {
      const response = await login(formData.email, formData.password);
      
      console.log('Respuesta del servidor:', response); // Debug
      
      if (response.success) {
        alert('✅ Login exitoso');
        onLogin(response.data);
        window.location.hash = '#home';
      } else {
        setError(response.message || 'Error al iniciar sesión');
      }
    } catch (err) {
      console.error('Error completo:', err); // Debug
      const errorMsg = err.response?.data?.message || 'Credenciales incorrectas';
      setError(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>Iniciar Sesión</h2>
        
        {error && <div className="error-message">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="email">Correo Electrónico</label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
              placeholder="ejemplo@correo.com"
              autoComplete="email"
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Contraseña</label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              placeholder="Mínimo 6 caracteres"
              autoComplete="current-password"
            />
          </div>

          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
          </button>
        </form>

        <div className="auth-footer">
          <p>¿No tienes cuenta? <a href="#register">Regístrate aquí</a></p>
        </div>
      </div>
    </div>
  );
};

export default Login;