package com.example.sistemaventas.service;

import com.example.sistemaventas.dto.ProductoRequest;
import com.example.sistemaventas.exception.ResourceNotFoundException;
import com.example.sistemaventas.model.Producto;
import com.example.sistemaventas.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    // Crear nuevo producto
    @Transactional
    public Producto crearProducto(ProductoRequest request) {
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecioPen(request.getPrecioPen());
        producto.setPrecioUsd(request.getPrecioUsd());
        producto.setStock(request.getStock());
        producto.setImagen(request.getImagen());
        producto.setCategoria(request.getCategoria());
        producto.setActivo(true);

        return productoRepository.save(producto);
    }

    // Obtener producto por ID
    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
    }

    // Listar todos los productos activos
    public List<Producto> listarProductosActivos() {
        return productoRepository.findByActivoTrue();
    }

    // Listar todos los productos (incluye inactivos)
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    // Buscar productos por categoría
    public List<Producto> buscarPorCategoria(String categoria) {
        return productoRepository.findByCategoriaAndActivoTrue(categoria);
    }

    // Buscar productos por nombre
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
    }

    // Actualizar producto
    @Transactional
    public Producto actualizarProducto(Long id, ProductoRequest request) {
        Producto producto = obtenerProductoPorId(id);

        if (request.getNombre() != null) {
            producto.setNombre(request.getNombre());
        }
        if (request.getDescripcion() != null) {
            producto.setDescripcion(request.getDescripcion());
        }
        if (request.getPrecioPen() != null) {
            producto.setPrecioPen(request.getPrecioPen());
        }
        if (request.getPrecioUsd() != null) {
            producto.setPrecioUsd(request.getPrecioUsd());
        }
        if (request.getStock() != null) {
            producto.setStock(request.getStock());
        }
        if (request.getImagen() != null) {
            producto.setImagen(request.getImagen());
        }
        if (request.getCategoria() != null) {
            producto.setCategoria(request.getCategoria());
        }

        return productoRepository.save(producto);
    }

    // Actualizar stock de producto
    @Transactional
    public void actualizarStock(Long id, Integer cantidad) {
        Producto producto = obtenerProductoPorId(id);
        
        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getStock());
        }
        
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }

    // Desactivar producto (eliminación lógica)
    @Transactional
    public void desactivarProducto(Long id) {
        Producto producto = obtenerProductoPorId(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    // Eliminar producto físicamente
    @Transactional
    public void eliminarProducto(Long id) {
        Producto producto = obtenerProductoPorId(id);
        productoRepository.delete(producto);
    }

    // Verificar disponibilidad de stock
    public boolean verificarStock(Long productoId, Integer cantidad) {
        Producto producto = obtenerProductoPorId(productoId);
        return producto.getStock() >= cantidad;
    }
}