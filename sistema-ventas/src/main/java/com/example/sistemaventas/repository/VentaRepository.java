package com.example.sistemaventas.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.sistemaventas.model.Usuario;
import com.example.sistemaventas.model.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    
    // Buscar ventas por usuario
    List<Venta> findByUsuarioOrderByFechaVentaDesc(Usuario usuario);
    
    // Buscar venta por número de comprobante
    Optional<Venta> findByNumeroComprobante(String numeroComprobante);
    
    // Verificar si existe un número de comprobante
    Boolean existsByNumeroComprobante(String numeroComprobante);
    
    // Buscar ventas por rango de fechas
    List<Venta> findByFechaVentaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    
    // Buscar últimas ventas de un usuario
    List<Venta> findTop10ByUsuarioOrderByFechaVentaDesc(Usuario usuario);
    
    // Contar ventas del día
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.fechaVenta = CURRENT_DATE")
    Long contarVentasHoy();
    
    // Generar siguiente número de comprobante
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.tipoComprobante = ?1")
    Long contarPorTipoComprobante(String tipoComprobante);
}