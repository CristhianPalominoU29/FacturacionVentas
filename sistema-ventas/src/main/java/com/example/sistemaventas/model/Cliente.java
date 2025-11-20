package com.example.sistemaventas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "tipo_documento", nullable = false, length = 20)
    private String tipoDocumento; // DNI o RUC
    
    @Column(name = "numero_documento", nullable = false, unique = true, length = 20)
    private String numeroDocumento;
    
    @Column(name = "razon_social", length = 200)
    private String razonSocial; // Para RUC (empresas)
    
    @Column(length = 200)
    private String nombres; // Para DNI (personas)
    
    @Column(name = "apellido_paterno", length = 100)
    private String apellidoPaterno;
    
    @Column(name = "apellido_materno", length = 100)
    private String apellidoMaterno;
    
    @Column(length = 300)
    private String direccion;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(length = 100)
    private String email;


}