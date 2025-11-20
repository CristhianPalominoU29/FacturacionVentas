import { useLocalStorage } from './useLocalStorage';

export const useCarrito = () => {
  const [carrito, setCarrito] = useLocalStorage('carrito', []);

  const agregarAlCarrito = (producto, cantidad = 1) => {
    setCarrito(prevCarrito => {
      const existe = prevCarrito.find(item => item.id === producto.id);
      
      if (existe) {
        return prevCarrito.map(item =>
          item.id === producto.id
            ? { ...item, cantidad: item.cantidad + cantidad }
            : item
        );
      }
      
      return [...prevCarrito, { ...producto, cantidad }];
    });
  };

  const eliminarDelCarrito = (productoId) => {
    setCarrito(prevCarrito => prevCarrito.filter(item => item.id !== productoId));
  };

  const actualizarCantidad = (productoId, cantidad) => {
    if (cantidad <= 0) {
      eliminarDelCarrito(productoId);
      return;
    }
    
    setCarrito(prevCarrito =>
      prevCarrito.map(item =>
        item.id === productoId ? { ...item, cantidad } : item
      )
    );
  };

  const vaciarCarrito = () => {
    setCarrito([]);
  };

  const calcularTotal = (moneda = 'PEN') => {
    return carrito.reduce((total, item) => {
      const precio = moneda === 'PEN' ? item.precioPen : item.precioUsd;
      return total + (precio * item.cantidad);
    }, 0);
  };

  const cantidadTotal = carrito.reduce((total, item) => total + item.cantidad, 0);

  return {
    carrito,
    agregarAlCarrito,
    eliminarDelCarrito,
    actualizarCantidad,
    vaciarCarrito,
    calcularTotal,
    cantidadTotal
  };
};