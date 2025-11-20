import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

// Auth
export const register = async (nombre, email, password) => {
  const response = await axios.post(`${API_URL}/auth/register`, {
    nombre,
    email,
    password
  });
  return response.data;
};

export const login = async (email, password) => {
  const response = await axios.post(`${API_URL}/auth/login`, {
    email,
    password
  });
  return response.data;
};

// Productos
export const getProductos = async () => {
  const response = await axios.get(`${API_URL}/productos`);
  return response.data;
};

// Clientes
export const consultarDNI = async (dni) => {
  const response = await axios.get(`${API_URL}/clientes/consultar/dni/${dni}`);
  return response.data;
};

export const consultarRUC = async (ruc) => {
  const response = await axios.get(`${API_URL}/clientes/consultar/ruc/${ruc}`);
  return response.data;
};

export const guardarCliente = async (clienteData) => {
  const response = await axios.post(`${API_URL}/clientes`, clienteData);
  return response.data;
};

// Ventas
export const crearVenta = async (ventaData) => {
  const response = await axios.post(`${API_URL}/ventas`, ventaData);
  return response.data;
};

// PDF
export const descargarPDF = async (ventaId) => {
  const response = await axios.get(`${API_URL}/pdf/comprobante/${ventaId}`, {
    responseType: 'blob'
  });
  return response.data;
};