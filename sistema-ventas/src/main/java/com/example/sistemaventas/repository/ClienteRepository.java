package com.example.sistemaventas.repository;

import com.example.sistemaventas.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    // Buscar cliente por número de documento
    Optional<Cliente> findByNumeroDocumento(String numeroDocumento);
    
    // Verificar si existe un documento
    Boolean existsByNumeroDocumento(String numeroDocumento);
    
    // Buscar por tipo de documento y número
    Optional<Cliente> findByTipoDocumentoAndNumeroDocumento(String tipoDocumento, String numeroDocumento);
}