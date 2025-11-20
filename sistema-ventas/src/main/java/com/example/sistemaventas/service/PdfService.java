package com.example.sistemaventas.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.sistemaventas.model.Venta;
import com.example.sistemaventas.util.PdfGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final VentaService ventaService;
    private final PdfGenerator pdfGenerator;

    @Value("${app.pdf.directory:src/main/resources/static/pdfs}")
    private String pdfDirectory;

    // 1. GUARDA EN DISCO + DEVUELVE RUTA (usado al crear venta)
    public String generarYGuardarComprobantePdf(Long ventaId) {
        Venta venta = ventaService.obtenerVentaPorId(ventaId);
        ByteArrayOutputStream baos = pdfGenerator.generarComprobante(venta);

        File dir = new File(pdfDirectory);
        if (!dir.exists()) dir.mkdirs();

        String fileName = "comprobante-" + ventaId + ".pdf";
        String filePath = pdfDirectory + File.separator + fileName;

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            baos.writeTo(fos);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar PDF", e);
        }

        String rutaPublica = "/pdfs/" + fileName;
        ventaService.marcarPdfGenerado(ventaId, rutaPublica);

        return rutaPublica;
    }

    // 2. SOLO GENERA EN MEMORIA (usado por PdfController para vista previa)
    public byte[] generarComprobantePdf(Long ventaId) {
        Venta venta = ventaService.obtenerVentaPorId(ventaId);
        ByteArrayOutputStream baos = pdfGenerator.generarComprobante(venta);
        return baos.toByteArray();
    }
}