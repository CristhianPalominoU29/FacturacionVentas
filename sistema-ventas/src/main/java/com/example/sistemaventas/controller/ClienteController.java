package com.example.sistemaventas.controller;

import com.example.sistemaventas.dto.ApiResponse;
import com.example.sistemaventas.dto.ClienteRequest;
import com.example.sistemaventas.model.Cliente;
import com.example.sistemaventas.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    // Crear o actualizar cliente
    @PostMapping
    public ResponseEntity<ApiResponse<Cliente>> guardar(
            @Valid @RequestBody ClienteRequest request) {
        try {
            Cliente cliente = clienteService.guardarCliente(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Cliente guardado exitosamente", cliente));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Consultar DNI desde API externa
    @GetMapping("/consultar/dni/{dni}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> consultarDNI(
            @PathVariable String dni) {
        try {
            Map<String, Object> datos = clienteService.consultarDNI(dni);
            return ResponseEntity.ok(ApiResponse.success("DNI consultado exitosamente", datos));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Consultar RUC desde API externa
    @GetMapping("/consultar/ruc/{ruc}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> consultarRUC(
            @PathVariable String ruc) {
        try {
            Map<String, Object> datos = clienteService.consultarRUC(ruc);
            return ResponseEntity.ok(ApiResponse.success("RUC consultado exitosamente", datos));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Obtener cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cliente>> obtenerPorId(@PathVariable Long id) {
        try {
            Cliente cliente = clienteService.obtenerClientePorId(id);
            return ResponseEntity.ok(ApiResponse.success("Cliente encontrado", cliente));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Obtener cliente por documento
    @GetMapping("/documento/{numeroDocumento}")
    public ResponseEntity<ApiResponse<Cliente>> obtenerPorDocumento(
            @PathVariable String numeroDocumento) {
        try {
            Cliente cliente = clienteService.obtenerClientePorDocumento(numeroDocumento);
            return ResponseEntity.ok(ApiResponse.success("Cliente encontrado", cliente));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Listar todos los clientes
    @GetMapping
    public ResponseEntity<ApiResponse<List<Cliente>>> listarTodos() {
        List<Cliente> clientes = clienteService.listarTodos();
        return ResponseEntity.ok(ApiResponse.success("Clientes obtenidos", clientes));
    }

    // Eliminar cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        try {
            clienteService.eliminarCliente(id);
            return ResponseEntity.ok(ApiResponse.success("Cliente eliminado", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}