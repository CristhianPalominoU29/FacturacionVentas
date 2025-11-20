package com.example.sistemaventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String nombre;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "precio_pen", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioPen;
    
    @Column(name = "precio_usd", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUsd;
    
    @Column(nullable = false)
    private Integer stock = 0;
    
    @Column(length = 500)
    private String imagen;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(length = 100)
    private String categoria;
}