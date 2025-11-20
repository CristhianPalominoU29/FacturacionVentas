package com.example.sistemaventas.repository;

import com.example.sistemaventas.model.DetalleVenta;
import com.example.sistemaventas.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    
    // Buscar detalles por venta
    List<DetalleVenta> findByVenta(Venta venta);
    
    // Buscar detalles por venta ID
    List<DetalleVenta> findByVentaId(Long ventaId);
    
    // Productos más vendidos
    @Query("SELECT d.producto.nombre, SUM(d.cantidad) as total " +
           "FROM DetalleVenta d " +
           "GROUP BY d.producto.id, d.producto.nombre " +
           "ORDER BY total DESC")
    List<Object[]> findProductosMasVendidos();
}