package com.example.sistemaventas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaRequest {
    
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;
    
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;
    
    @NotBlank(message = "El tipo de comprobante es obligatorio")
    @Pattern(regexp = "BOLETA|FACTURA", message = "El tipo de comprobante debe ser BOLETA o FACTURA")
    private String tipoComprobante;
    
    @NotBlank(message = "La moneda es obligatoria")
    @Pattern(regexp = "PEN|USD", message = "La moneda debe ser PEN o USD")
    private String moneda;
    
    @NotEmpty(message = "Debe haber al menos un producto en la venta")
    @Valid
    private List<DetalleVentaRequest> detalles;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleVentaRequest {
        
        @NotNull(message = "El ID del producto es obligatorio")
        private Long productoId;
        
        @NotNull(message = "La cantidad es obligatoria")
        private Integer cantidad;
    }
}