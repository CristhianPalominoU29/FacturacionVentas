package com.example.sistemaventas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.sistemaventas.dto.ApiResponse;
import com.example.sistemaventas.dto.ProductoRequest;
import com.example.sistemaventas.model.Producto;
import com.example.sistemaventas.service.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<ApiResponse<Producto>> crear(@Valid @RequestBody ProductoRequest request) {
        try {
            Producto producto = productoService.crearProducto(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Producto creado exitosamente", producto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ← AQUÍ ESTABA EL PROBLEMA: tu frontend espera solo "data", no "message"
    @GetMapping
    public ResponseEntity<List<Producto>> listarActivos() {  // ← DEVUELVE LISTA DIRECTA
        List<Producto> productos = productoService.listarProductosActivos();
        return ResponseEntity.ok(productos); // ← SIN ApiResponse, solo la lista
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Producto>> listarTodos() {
        List<Producto> productos = productoService.listarTodos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        Producto producto = productoService.obtenerProductoPorId(id);
        return ResponseEntity.ok(producto);
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Producto>> buscarPorCategoria(@PathVariable String categoria) {
        List<Producto> productos = productoService.buscarPorCategoria(categoria);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarPorNombre(@RequestParam String nombre) {
        List<Producto> productos = productoService.buscarPorNombre(nombre);
        return ResponseEntity.ok(productos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        Producto producto = productoService.actualizarProducto(id, request);
        return ResponseEntity.ok(producto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        productoService.desactivarProducto(id);
        return ResponseEntity.ok().build();
    }
}