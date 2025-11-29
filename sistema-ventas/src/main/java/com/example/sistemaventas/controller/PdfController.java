package com.example.sistemaventas.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sistemaventas.service.PdfService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PdfController {

    private final PdfService pdfService;

    @GetMapping("/comprobante/{ventaId}")
    public ResponseEntity<byte[]> generarComprobante(@PathVariable Long ventaId) {
        try {
            System.out.println("Generando PDF para venta ID: " + ventaId);
            
            byte[] pdfBytes = pdfService.generarComprobantePdf(ventaId);
            
            System.out.println("PDF generado. Tamaño: " + pdfBytes.length + " bytes");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "comprobante_" + ventaId + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            System.err.println("Error al generar PDF: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/comprobante/{ventaId}/preview")
    public ResponseEntity<byte[]> previsualizarComprobante(@PathVariable Long ventaId) {
        try {
            byte[] pdfBytes = pdfService.generarComprobantePdf(ventaId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "comprobante_" + ventaId + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error al generar PDF: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}