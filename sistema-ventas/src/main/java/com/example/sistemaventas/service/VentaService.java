package com.example.sistemaventas.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sistemaventas.dto.VentaRequest;
import com.example.sistemaventas.exception.ResourceNotFoundException;
import com.example.sistemaventas.model.Cliente;
import com.example.sistemaventas.model.DetalleVenta;
import com.example.sistemaventas.model.Producto;
import com.example.sistemaventas.model.Usuario;
import com.example.sistemaventas.model.Venta;
import com.example.sistemaventas.repository.VentaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final UsuarioService usuarioService;
    private final ClienteService clienteService;
    private final ProductoService productoService;

    @Transactional
    public Venta crearVenta(VentaRequest request) {
        try {
            System.out.println("=== INICIANDO CREACIÓN DE VENTA ===");
            System.out.println("Usuario ID: " + request.getUsuarioId());
            System.out.println("Cliente ID: " + request.getClienteId());
            
            // Obtener usuario
            Usuario usuario = usuarioService.obtenerUsuarioPorId(request.getUsuarioId());
            System.out.println("Usuario encontrado: " + usuario.getNombre());

            // Obtener cliente
            Cliente cliente = clienteService.obtenerClientePorId(request.getClienteId());
            System.out.println("Cliente encontrado: " + cliente.getNumeroDocumento());

            // Crear venta
            Venta venta = new Venta();
            venta.setUsuario(usuario);
            venta.setCliente(cliente);
            venta.setTipoComprobante(request.getTipoComprobante());
            venta.setMoneda(request.getMoneda());

            // Generar número de comprobante
            String numeroComprobante = generarNumeroComprobante(request.getTipoComprobante());
            venta.setNumeroComprobante(numeroComprobante);
            System.out.println("Número de comprobante: " + numeroComprobante);

            // Procesar detalles
            BigDecimal subtotal = BigDecimal.ZERO;
            List<DetalleVenta> detalles = new ArrayList<>();

            System.out.println("Procesando " + request.getDetalles().size() + " productos");
            
            for (VentaRequest.DetalleVentaRequest detalleRequest : request.getDetalles()) {
                Producto producto = productoService.obtenerProductoPorId(detalleRequest.getProductoId());
                System.out.println("Procesando producto: " + producto.getNombre());

                // Verificar stock
                if (!productoService.verificarStock(producto.getId(), detalleRequest.getCantidad())) {
                    throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
                }

                // Crear detalle
                DetalleVenta detalle = new DetalleVenta();
                detalle.setVenta(venta);
                detalle.setProducto(producto);
                detalle.setCantidad(detalleRequest.getCantidad());

                // Precio según moneda
                BigDecimal precioUnitario = request.getMoneda().equals("PEN") 
                        ? producto.getPrecioPen() 
                        : producto.getPrecioUsd();
                
                detalle.setPrecioUnitario(precioUnitario);
                
                BigDecimal subtotalDetalle = precioUnitario.multiply(new BigDecimal(detalleRequest.getCantidad()));
                detalle.setSubtotal(subtotalDetalle);

                detalles.add(detalle);
                subtotal = subtotal.add(subtotalDetalle);

                // Actualizar stock
                productoService.actualizarStock(producto.getId(), detalleRequest.getCantidad());
            }

            // Calcular IGV y total
            BigDecimal igv = subtotal.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal total = subtotal.add(igv);

            venta.setSubtotal(subtotal);
            venta.setIgv(igv);
            venta.setTotal(total);
            venta.setDetalles(detalles);

            System.out.println("Subtotal: " + subtotal);
            System.out.println("IGV: " + igv);
            System.out.println("Total: " + total);

            Venta ventaGuardada = ventaRepository.save(venta);
            System.out.println("=== VENTA GUARDADA EXITOSAMENTE - ID: " + ventaGuardada.getId() + " ===");
            
            return ventaGuardada;
            
        } catch (Exception e) {
            System.err.println("=== ERROR AL CREAR VENTA ===");
            System.err.println("Tipo de error: " + e.getClass().getName());
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al crear la venta: " + e.getMessage(), e);
        }
    }

    private String generarNumeroComprobante(String tipoComprobante) {
        Long contador = ventaRepository.contarPorTipoComprobante(tipoComprobante);
        String prefijo = tipoComprobante.equals("BOLETA") ? "B001" : "F001";
        String numero = String.format("%08d", contador + 1);
        return prefijo + "-" + numero;
    }

    public Venta obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con ID: " + id));
    }

    public Venta obtenerVentaPorNumeroComprobante(String numeroComprobante) {
        return ventaRepository.findByNumeroComprobante(numeroComprobante)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con número: " + numeroComprobante));
    }

    public List<Venta> listarVentasPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(usuarioId);
        return ventaRepository.findByUsuarioOrderByFechaVentaDesc(usuario);
    }

    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }

    @Transactional
    public void marcarPdfGenerado(Long ventaId, String rutaPdf) {
        Venta venta = obtenerVentaPorId(ventaId);
        venta.setPdfGenerado(true);
        venta.setRutaPdf(rutaPdf);
        ventaRepository.save(venta);
    }
}