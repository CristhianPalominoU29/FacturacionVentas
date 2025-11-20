package com.example.sistemaventas.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sistemaventas.dto.ApiResponse;
import com.example.sistemaventas.dto.LoginRequest;
import com.example.sistemaventas.dto.RegisterRequest;
import com.example.sistemaventas.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> registrar(
            @Valid @RequestBody RegisterRequest request) {
        try {
            Map<String, Object> usuario = usuarioService.registrarUsuario(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Usuario registrado exitosamente", usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
            @Valid @RequestBody LoginRequest request) {
        try {
            Map<String, Object> usuario = usuarioService.loginUsuario(request);
            return ResponseEntity.ok(ApiResponse.success("Login exitoso", usuario));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Credenciales inválidas"));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verify() {
        return ResponseEntity.ok(ApiResponse.success("Token válido", "OK"));
    }
}