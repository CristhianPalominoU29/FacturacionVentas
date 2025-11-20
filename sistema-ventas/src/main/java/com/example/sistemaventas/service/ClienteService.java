package com.example.sistemaventas.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.example.sistemaventas.dto.ClienteRequest;
import com.example.sistemaventas.exception.ResourceNotFoundException;
import com.example.sistemaventas.model.Cliente;
import com.example.sistemaventas.repository.ClienteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    // Tu API Key
    private static final String API_TOKEN = "sk_11022.AGypD7pqkqctIYZ95RuVwAaLFaJZC53k";

    // Crear o actualizar cliente
    @Transactional
    public Cliente guardarCliente(ClienteRequest request) {
        // Buscar si ya existe el cliente
        Cliente cliente = clienteRepository.findByNumeroDocumento(request.getNumeroDocumento())
                .orElse(new Cliente());

        cliente.setTipoDocumento(request.getTipoDocumento());
        cliente.setNumeroDocumento(request.getNumeroDocumento());
        cliente.setRazonSocial(request.getRazonSocial());
        cliente.setNombres(request.getNombres());
        cliente.setApellidoPaterno(request.getApellidoPaterno());
        cliente.setApellidoMaterno(request.getApellidoMaterno());
        cliente.setDireccion(request.getDireccion());
        cliente.setTelefono(request.getTelefono());
        cliente.setEmail(request.getEmail());

        return clienteRepository.save(cliente);
    }

    // Consultar DNI desde API externa CON TOKEN
    // Consultar RUC desde DECOLECTA (tu API real)
    public Map<String, Object> consultarRUC(String ruc) {
        try {
            String url = "https://api.decolecta.com/v1/sunat/ruc?numero=" + ruc;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + API_TOKEN);  
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map.class
            );

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar RUC: " + e.getMessage());
        }
    }

// Consultar DNI desde DECOLECTA
    public Map<String, Object> consultarDNI(String dni) {
        try {
            String url = "https://api.decolecta.com/v1/reniec/dni?numero=" + dni;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + API_TOKEN);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map.class
            );

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar DNI: " + e.getMessage());
        }
    }

    // Obtener cliente por ID
    public Cliente obtenerClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));
    }

    // Obtener cliente por número de documento
    public Cliente obtenerClientePorDocumento(String numeroDocumento) {
        return clienteRepository.findByNumeroDocumento(numeroDocumento)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con documento: " + numeroDocumento));
    }

    // Listar todos los clientes
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    // Eliminar cliente
    @Transactional
    public void eliminarCliente(Long id) {
        Cliente cliente = obtenerClientePorId(id);
        clienteRepository.delete(cliente);
    }
}
