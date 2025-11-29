import React, { useState } from 'react';
import { consultarDNI, consultarRUC, guardarCliente, crearVenta, descargarPDF } from '../services/api';
import './Checkout.css';

const Checkout = ({ user, carrito, vaciarCarrito, calcularTotal }) => {
  const [paso, setPaso] = useState(1);
  const [tipoComprobante, setTipoComprobante] = useState('BOLETA');
  const [moneda, setMoneda] = useState('PEN');
  const [documento, setDocumento] = useState('');
  const [clienteData, setClienteData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [ventaId, setVentaId] = useState(null);
  const [totalPagado, setTotalPagado] = useState(0);

  const total = calcularTotal(moneda);
  const simbolo = moneda === 'PEN' ? 'S/' : '$';
  const subtotal = total / 1.18;
  const igv = total - subtotal;

  const handleConsultarDocumento = async () => {
    setError('');
    setLoading(true);

    try {
      let response;
      if (tipoComprobante === 'BOLETA') {
        if (documento.length !== 8) {
          setError('El DNI debe tener 8 dígitos');
          setLoading(false);
          return;
        }
        response = await consultarDNI(documento);
      } else {
        if (documento.length !== 11) {
          setError('El RUC debe tener 11 dígitos');
          setLoading(false);
          return;
        }
        response = await consultarRUC(documento);
      }

      console.log('Respuesta de la API:', response);

      if (response.success) {
        const data = response.data;
        
        if (tipoComprobante === 'BOLETA') {
          setClienteData({
            tipoDocumento: 'DNI',
            numeroDocumento: documento,
            nombres: data.first_name || data.nombres || '',
            apellidoPaterno: data.first_last_name || data.apellidoPaterno || '',
            apellidoMaterno: data.second_last_name || data.apellidoMaterno || '',
            direccion: ''
          });
        } else {
          setClienteData({
            tipoDocumento: 'RUC',
            numeroDocumento: documento,
            razonSocial: data.razon_social || data.nombre || '',
            direccion: data.direccion || ''
          });
        }
      } else {
        setError('No se encontraron datos para este documento');
      }
    } catch (err) {
      console.error('Error completo:', err);
      setError('Error al consultar el documento. Verifica que sea correcto.');
    } finally {
      setLoading(false);
    }
  };

  const handleProcesarCompra = async () => {
    setError('');
    setLoading(true);

    try {
      const totalActual = calcularTotal(moneda);
      setTotalPagado(totalActual);

      const clienteResponse = await guardarCliente(clienteData);
      
      if (!clienteResponse.success) {
        setError('Error al guardar los datos del cliente');
        setLoading(false);
        return;
      }

      const clienteId = clienteResponse.data.id;

      const ventaData = {
        usuarioId: user.id,
        clienteId: clienteId,
        tipoComprobante: tipoComprobante,
        moneda: moneda,
        detalles: carrito.map(item => ({
          productoId: item.id,
          cantidad: item.cantidad
        }))
      };

      const ventaResponse = await crearVenta(ventaData);

      if (ventaResponse.success) {
        setVentaId(ventaResponse.data.id);
        setPaso(3);
        vaciarCarrito(); 
      } else {
        setError('Error al procesar la venta');
      }
    } catch (err) {
      console.error('Error al procesar compra:', err);
      setError('Error al procesar la compra. Intenta nuevamente.');
    } finally {
      setLoading(false);
    }
  };

  const handleDescargarPDF = async () => {
    try {
      const pdfBlob = await descargarPDF(ventaId);
      const url = window.URL.createObjectURL(pdfBlob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `comprobante_${ventaId}.pdf`;
      link.click();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      alert('Error al descargar el PDF');
      console.error(err);
    }
  };

  if (carrito.length === 0 && paso !== 3) {
    return (
      <div className="checkout-vacio">
        <h2>No hay productos en el carrito</h2>
        <a href="#home" className="btn-volver">Ir a la tienda</a>
      </div>
    );
  }

  if (paso === 3) {
    return (
      <div className="checkout-exito">
        <div className="exito-card">
          <div className="exito-icono">✅</div>
          <h2>¡Compra realizada con éxito!</h2>
          <p>Tu {tipoComprobante.toLowerCase()} ha sido generada correctamente</p>
          
          <div className="exito-info">
            <p><strong>Total pagado:</strong> {simbolo} {totalPagado.toFixed(2)}</p>
            <p><strong>ID de venta:</strong> #{ventaId}</p>
            <p><strong>Tipo de comprobante:</strong> {tipoComprobante}</p>
          </div>

          <div className="exito-acciones">
            <button onClick={handleDescargarPDF} className="btn-descargar-pdf">
              📄 Descargar {tipoComprobante}
            </button>
            <a href="#home" className="btn-volver-tienda">
              Volver a la tienda
            </a>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="checkout-container">
      <div className="checkout-header">
        <h1>Finalizar Compra</h1>
        <div className="pasos-indicador">
          <div className={`paso ${paso >= 1 ? 'activo' : ''}`}>
            <span className="paso-numero">1</span>
            <span className="paso-texto">Datos</span>
          </div>
          <div className={`paso ${paso >= 2 ? 'activo' : ''}`}>
            <span className="paso-numero">2</span>
            <span className="paso-texto">Confirmación</span>
          </div>
          <div className={`paso ${paso >= 3 ? 'activo' : ''}`}>
            <span className="paso-numero">3</span>
            <span className="paso-texto">Listo</span>
          </div>
        </div>
      </div>

      <div className="checkout-content">
        <div className="checkout-form">
          {error && <div className="error-message">{error}</div>}

          {paso === 1 && (
            <>
              <div className="form-section">
                <h3>1. Tipo de Comprobante</h3>
                <div className="comprobante-opciones">
                  <label className={`opcion-card ${tipoComprobante === 'BOLETA' ? 'seleccionado' : ''}`}>
                    <input
                      type="radio"
                      name="tipoComprobante"
                      value="BOLETA"
                      checked={tipoComprobante === 'BOLETA'}
                      onChange={(e) => {
                        setTipoComprobante(e.target.value);
                        setDocumento('');
                        setClienteData(null);
                      }}
                    />
                    <div className="opcion-content">
                      <strong>🧾 Boleta</strong>
                      <small>Para personas (DNI)</small>
                    </div>
                  </label>

                  <label className={`opcion-card ${tipoComprobante === 'FACTURA' ? 'seleccionado' : ''}`}>
                    <input
                      type="radio"
                      name="tipoComprobante"
                      value="FACTURA"
                      checked={tipoComprobante === 'FACTURA'}
                      onChange={(e) => {
                        setTipoComprobante(e.target.value);
                        setDocumento('');
                        setClienteData(null);
                      }}
                    />
                    <div className="opcion-content">
                      <strong>📄 Factura</strong>
                      <small>Para empresas (RUC)</small>
                    </div>
                  </label>
                </div>
              </div>

              <div className="form-section">
                <h3>2. {tipoComprobante === 'BOLETA' ? 'DNI' : 'RUC'}</h3>
                <div className="documento-input-group">
                  <input
                    type="text"
                    placeholder={tipoComprobante === 'BOLETA' ? 'Ingresa tu DNI (8 dígitos)' : 'Ingresa tu RUC (11 dígitos)'}
                    value={documento}
                    onChange={(e) => setDocumento(e.target.value.replace(/\D/g, ''))}
                    maxLength={tipoComprobante === 'BOLETA' ? 8 : 11}
                    className="input-documento"
                  />
                  <button 
                    onClick={handleConsultarDocumento}
                    disabled={loading || (tipoComprobante === 'BOLETA' ? documento.length !== 8 : documento.length !== 11)}
                    className="btn-consultar"
                  >
                    {loading ? 'Consultando...' : 'Consultar'}
                  </button>
                </div>
              </div>

              {clienteData && (
                <div className="form-section datos-cliente">
                  <h3>3. Datos del Cliente</h3>
                  {tipoComprobante === 'BOLETA' ? (
                    <>
                      <div className="dato-row">
                        <label>Nombres:</label>
                        <span>{clienteData.nombres}</span>
                      </div>
                      <div className="dato-row">
                        <label>Apellido Paterno:</label>
                        <span>{clienteData.apellidoPaterno}</span>
                      </div>
                      <div className="dato-row">
                        <label>Apellido Materno:</label>
                        <span>{clienteData.apellidoMaterno}</span>
                      </div>
                    </>
                  ) : (
                    <>
                      <div className="dato-row">
                        <label>Razón Social:</label>
                        <span>{clienteData.razonSocial}</span>
                      </div>
                      <div className="dato-row">
                        <label>Dirección:</label>
                        <span>{clienteData.direccion || 'No disponible'}</span>
                      </div>
                    </>
                  )}
                </div>
              )}

              <div className="form-section">
                <h3>4. Moneda</h3>
                <select 
                  value={moneda} 
                  onChange={(e) => setMoneda(e.target.value)}
                  className="select-moneda"
                >
                  <option value="PEN">Soles (S/)</option>
                  <option value="USD">Dólares ($)</option>
                </select>
              </div>

              <div className="form-actions">
                <a href="#carrito" className="btn-secondary">
                  ← Volver al carrito
                </a>
                <button
                  onClick={() => setPaso(2)}
                  disabled={!clienteData}
                  className="btn-primary"
                >
                  Continuar →
                </button>
              </div>
            </>
          )}

          {paso === 2 && (
            <>
              <div className="confirmacion-datos">
                <h3>Confirma tu compra</h3>
                
                <div className="confirmacion-section">
                  <h4>Datos del comprobante:</h4>
                  <p><strong>Tipo:</strong> {tipoComprobante}</p>
                  <p><strong>Documento:</strong> {clienteData.numeroDocumento}</p>
                  {tipoComprobante === 'BOLETA' ? (
                    <p><strong>Cliente:</strong> {clienteData.nombres} {clienteData.apellidoPaterno} {clienteData.apellidoMaterno}</p>
                  ) : (
                    <p><strong>Razón Social:</strong> {clienteData.razonSocial}</p>
                  )}
                  <p><strong>Moneda:</strong> {moneda === 'PEN' ? 'Soles' : 'Dólares'}</p>
                </div>

                <div className="confirmacion-section">
                  <h4>Productos ({carrito.length}):</h4>
                  <ul className="lista-productos-confirmar">
                    {carrito.map(item => (
                      <li key={item.id}>
                        {item.nombre} x {item.cantidad}
                      </li>
                    ))}
                  </ul>
                </div>
              </div>

              <div className="form-actions">
                <button onClick={() => setPaso(1)} className="btn-secondary">
                  ← Volver
                </button>
                <button
                  onClick={handleProcesarCompra}
                  disabled={loading}
                  className="btn-primary btn-confirmar"
                >
                  {loading ? 'Procesando...' : '✓ Confirmar Compra'}
                </button>
              </div>
            </>
          )}
        </div>

        <div className="checkout-resumen">
          <h3>Resumen de compra</h3>
          
          <div className="resumen-productos">
            {carrito.map(item => {
              const precio = moneda === 'PEN' ? item.precioPen : item.precioUsd;
              return (
                <div key={item.id} className="resumen-item">
                  <span>{item.nombre} x{item.cantidad}</span>
                  <span>{simbolo} {(precio * item.cantidad).toFixed(2)}</span>
                </div>
              );
            })}
          </div>

          <div className="resumen-totales">
            <div className="total-linea">
              <span>Subtotal:</span>
              <span>{simbolo} {subtotal.toFixed(2)}</span>
            </div>
            <div className="total-linea">
              <span>IGV (18%):</span>
              <span>{simbolo} {igv.toFixed(2)}</span>
            </div>
            <div className="total-final">
              <span>Total:</span>
              <span>{simbolo} {total.toFixed(2)}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Checkout;