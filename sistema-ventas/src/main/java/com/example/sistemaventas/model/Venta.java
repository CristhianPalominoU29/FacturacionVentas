package com.example.sistemaventas.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @Column(name = "tipo_comprobante", nullable = false, length = 20)
    private String tipoComprobante; // BOLETA o FACTURA
    
    @Column(name = "numero_comprobante", unique = true, length = 50)
    private String numeroComprobante;
    
    @Column(nullable = false, length = 10)
    private String moneda; // PEN o USD
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal igv; // 18%
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @Column(name = "fecha_venta", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDate fechaVenta;
    
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();
    
    @Column(name = "pdf_generado")
    private Boolean pdfGenerado = false;
    
    @Column(name = "ruta_pdf", length = 500)
    private String rutaPdf;
}