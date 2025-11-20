package com.example.sistemaventas.service;

import com.example.sistemaventas.dto.LoginRequest;
import com.example.sistemaventas.dto.RegisterRequest;
import com.example.sistemaventas.exception.ResourceNotFoundException;
import com.example.sistemaventas.model.Usuario;
import com.example.sistemaventas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Registrar nuevo usuario
    @Transactional
    public Map<String, Object> registrarUsuario(RegisterRequest request) {
        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol("USER");
        usuario.setActivo(true);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Preparar respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("id", usuarioGuardado.getId());
        response.put("nombre", usuarioGuardado.getNombre());
        response.put("email", usuarioGuardado.getEmail());
        response.put("mensaje", "Usuario registrado exitosamente");

        return response;
    }

    // Login de usuario
    public Map<String, Object> loginUsuario(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailAndActivoTrue(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado o inactivo"));

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        // Preparar respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("id", usuario.getId());
        response.put("nombre", usuario.getNombre());
        response.put("email", usuario.getEmail());
        response.put("rol", usuario.getRol());
        response.put("mensaje", "Login exitoso");

        return response;
    }

    // Obtener usuario por ID
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    // Obtener usuario por email
    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }

    // Listar todos los usuarios
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // Actualizar usuario
    @Transactional
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        Usuario usuario = obtenerUsuarioPorId(id);

        if (usuarioActualizado.getNombre() != null) {
            usuario.setNombre(usuarioActualizado.getNombre());
        }
        if (usuarioActualizado.getEmail() != null) {
            usuario.setEmail(usuarioActualizado.getEmail());
        }

        return usuarioRepository.save(usuario);
    }

    // Desactivar usuario (eliminación lógica)
    @Transactional
    public void desactivarUsuario(Long id) {
        Usuario usuario = obtenerUsuarioPorId(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
}