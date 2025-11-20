package com.example.sistemaventas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaDocumentoResponse {
    
    private String tipoDocumento;
    private String numeroDocumento;
    
    // Para DNI
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    
    // Para RUC
    private String razonSocial;
    private String estado;
    private String condicion;
    private String direccion;
    private String departamento;
    private String provincia;
    private String distrito;
}