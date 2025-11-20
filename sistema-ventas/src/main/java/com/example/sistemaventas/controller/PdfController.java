package com.example.sistemaventas.controller;

import com.example.sistemaventas.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PdfController {

    private final PdfService pdfService;

    // ABRIR PDF EN NUEVA PESTAÑA (inline = se ve en el navegador)
    @GetMapping("/comprobante/{ventaId}/preview")
    public ResponseEntity<byte[]> previsualizarComprobante(@PathVariable Long ventaId) {
        try {
            byte[] pdfBytes = pdfService.generarComprobantePdf(ventaId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline()
                .filename("comprobante_" + ventaId + ".pdf")
                .build());
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // DESCARGAR PDF (opcional)
    @GetMapping("/comprobante/{ventaId}/download")
    public ResponseEntity<byte[]> descargarComprobante(@PathVariable Long ventaId) {
        try {
            byte[] pdfBytes = pdfService.generarComprobantePdf(ventaId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment()
                .filename("comprobante_" + ventaId + ".pdf")
                .build());

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}