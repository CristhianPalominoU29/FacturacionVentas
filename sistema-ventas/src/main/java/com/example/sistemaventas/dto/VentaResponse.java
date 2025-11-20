package com.example.sistemaventas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaResponse {
    
    private Long id;
    private String numeroComprobante;
    private String tipoComprobante;
    private String moneda;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private LocalDate fechaVenta;
    private ClienteInfo cliente;
    private List<DetalleInfo> detalles;

    private String rutaPdf;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClienteInfo {
        private Long id;
        private String tipoDocumento;
        private String numeroDocumento;
        private String nombreCompleto;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleInfo {
        private String productoNombre;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
    }
}