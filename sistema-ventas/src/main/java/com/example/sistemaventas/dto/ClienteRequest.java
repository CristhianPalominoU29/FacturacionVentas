package com.example.sistemaventas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequest {
    
    @NotBlank(message = "El tipo de documento es obligatorio")
    @Pattern(regexp = "DNI|RUC", message = "El tipo de documento debe ser DNI o RUC")
    private String tipoDocumento;
    
    @NotBlank(message = "El número de documento es obligatorio")
    private String numeroDocumento;
    
    // Para RUC (empresas)
    private String razonSocial;
    
    // Para DNI (personas)
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    
    private String direccion;
    private String telefono;
    private String email;
}