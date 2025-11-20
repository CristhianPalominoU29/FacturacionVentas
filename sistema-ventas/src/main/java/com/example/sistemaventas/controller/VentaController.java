package com.example.sistemaventas.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sistemaventas.dto.ApiResponse;
import com.example.sistemaventas.dto.VentaRequest;
import com.example.sistemaventas.dto.VentaResponse;
import com.example.sistemaventas.model.Venta;
import com.example.sistemaventas.service.PdfService;
import com.example.sistemaventas.service.VentaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;
    private final PdfService pdfService;

    // Crear nueva venta
    @PostMapping
    public ResponseEntity<ApiResponse<VentaResponse>> crear(@Valid @RequestBody VentaRequest request) {
        try {
            Venta venta = ventaService.crearVenta(request);

            // GENERAR Y GUARDAR PDF + DEVOLVER RUTA
            String rutaPdf = pdfService.generarYGuardarComprobantePdf(venta.getId());

            VentaResponse response = convertirAVentaResponse(venta);
            response.setRutaPdf(rutaPdf);  // ← Ahora sí funciona

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Venta creada exitosamente", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Obtener venta por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VentaResponse>> obtenerPorId(@PathVariable Long id) {
        try {
            Venta venta = ventaService.obtenerVentaPorId(id);
            VentaResponse response = convertirAVentaResponse(venta);
            return ResponseEntity.ok(ApiResponse.success("Venta encontrada", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Obtener venta por número de comprobante
    @GetMapping("/comprobante/{numeroComprobante}")
    public ResponseEntity<ApiResponse<VentaResponse>> obtenerPorComprobante(
            @PathVariable String numeroComprobante) {
        try {
            Venta venta = ventaService.obtenerVentaPorNumeroComprobante(numeroComprobante);
            VentaResponse response = convertirAVentaResponse(venta);
            return ResponseEntity.ok(ApiResponse.success("Venta encontrada", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Listar ventas por usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ApiResponse<List<VentaResponse>>> listarPorUsuario(
            @PathVariable Long usuarioId) {
        try {
            List<Venta> ventas = ventaService.listarVentasPorUsuario(usuarioId);
            List<VentaResponse> responses = ventas.stream()
                    .map(this::convertirAVentaResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Ventas obtenidas", responses));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Listar todas las ventas
    @GetMapping
    public ResponseEntity<ApiResponse<List<VentaResponse>>> listarTodas() {
        List<Venta> ventas = ventaService.listarTodas();
        List<VentaResponse> responses = ventas.stream()
                .map(this::convertirAVentaResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Ventas obtenidas", responses));
    }

    // Método auxiliar para convertir Venta a VentaResponse
    private VentaResponse convertirAVentaResponse(Venta venta) {
        VentaResponse response = new VentaResponse();
        response.setId(venta.getId());
        response.setNumeroComprobante(venta.getNumeroComprobante());
        response.setTipoComprobante(venta.getTipoComprobante());
        response.setMoneda(venta.getMoneda());
        response.setSubtotal(venta.getSubtotal());
        response.setIgv(venta.getIgv());
        response.setTotal(venta.getTotal());
        response.setFechaVenta(venta.getFechaVenta());
        response.setRutaPdf(venta.getRutaPdf());

        // Cliente info
        VentaResponse.ClienteInfo clienteInfo = new VentaResponse.ClienteInfo();
        clienteInfo.setId(venta.getCliente().getId());
        clienteInfo.setTipoDocumento(venta.getCliente().getTipoDocumento());
        clienteInfo.setNumeroDocumento(venta.getCliente().getNumeroDocumento());

        String nombreCompleto = venta.getCliente().getTipoDocumento().equals("RUC")
                ? venta.getCliente().getRazonSocial()
                : venta.getCliente().getNombres() + " "
                + venta.getCliente().getApellidoPaterno() + " "
                + venta.getCliente().getApellidoMaterno();
        clienteInfo.setNombreCompleto(nombreCompleto);
        response.setCliente(clienteInfo);

        // Detalles
        List<VentaResponse.DetalleInfo> detalles = venta.getDetalles().stream()
                .map(d -> {
                    VentaResponse.DetalleInfo info = new VentaResponse.DetalleInfo();
                    info.setProductoNombre(d.getProducto().getNombre());
                    info.setCantidad(d.getCantidad());
                    info.setPrecioUnitario(d.getPrecioUnitario());
                    info.setSubtotal(d.getSubtotal());
                    return info;
                })
                .collect(Collectors.toList());
        response.setDetalles(detalles);

        return response;
    }
}
