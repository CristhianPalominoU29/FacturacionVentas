import React, { useState } from 'react';
import { register } from '../services/api';
import './Auth.css';

const Register = ({ onLogin }) => {
  const [formData, setFormData] = useState({
    nombre: '',
    email: '',
    password: '',
    confirmPassword: ''
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

    // Validaciones del frontend
    if (!formData.nombre.trim()) {
      setError('El nombre es obligatorio');
      return;
    }

    if (formData.nombre.length < 3) {
      setError('El nombre debe tener al menos 3 caracteres');
      return;
    }

    if (!formData.email.includes('@')) {
      setError('El email debe ser válido');
      return;
    }

    if (formData.password.length < 6) {
      setError('La contraseña debe tener al menos 6 caracteres');
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      setError('Las contraseñas no coinciden');
      return;
    }

    setLoading(true);

    console.log('Intentando registrar:', formData); // Debug

    try {
      const response = await register(formData.nombre, formData.email, formData.password);
      
      console.log('Respuesta del servidor:', response); // Debug
      
      if (response.success) {
        alert('✅ Registro exitoso');
        onLogin(response.data);
        window.location.hash = '#home';
      } else {
        setError(response.message || 'Error al registrarse');
      }
    } catch (err) {
      console.error('Error completo:', err); // Debug
      const errorMsg = err.response?.data?.message || 'El email ya está registrado o hay un error en el servidor';
      setError(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>Crear Cuenta</h2>
        
        {error && <div className="error-message">{error}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="nombre">Nombre Completo *</label>
            <input
              type="text"
              id="nombre"
              name="nombre"
              value={formData.nombre}
              onChange={handleChange}
              required
              placeholder="Juan Pérez"
              minLength="3"
              autoComplete="name"
            />
          </div>

          <div className="form-group">
            <label htmlFor="email">Correo Electrónico *</label>
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
            <label htmlFor="password">Contraseña *</label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              placeholder="Mínimo 6 caracteres"
              minLength="6"
              autoComplete="new-password"
            />
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">Confirmar Contraseña *</label>
            <input
              type="password"
              id="confirmPassword"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              required
              placeholder="Repite tu contraseña"
              minLength="6"
              autoComplete="new-password"
            />
          </div>

          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'Registrando...' : 'Registrarse'}
          </button>
        </form>

        <div className="auth-footer">
          <p>¿Ya tienes cuenta? <a href="#login">Inicia sesión aquí</a></p>
        </div>
      </div>
    </div>
  );
};

export default Register;