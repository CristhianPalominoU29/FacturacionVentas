package com.example.sistemaventas.repository;

import com.example.sistemaventas.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // Buscar productos activos
    List<Producto> findByActivoTrue();
    
    // Buscar producto por ID y que esté activo
    Optional<Producto> findByIdAndActivoTrue(Long id);
    
    // Buscar productos por categoría
    List<Producto> findByCategoriaAndActivoTrue(String categoria);
    
    // Buscar productos por nombre (búsqueda parcial)
    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
    
    // Buscar productos con stock disponible
    List<Producto> findByActivoTrueAndStockGreaterThan(Integer stock);
}