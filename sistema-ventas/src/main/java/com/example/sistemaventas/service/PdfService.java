package com.example.sistemaventas.service;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

import com.example.sistemaventas.model.Venta;
import com.example.sistemaventas.util.PdfGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final VentaService ventaService;
    private final PdfGenerator pdfGenerator;

    public byte[] generarComprobantePdf(Long ventaId) {
        try {
            System.out.println("=== GENERANDO PDF PARA VENTA: " + ventaId + " ===");
            
            Venta venta = ventaService.obtenerVentaPorId(ventaId);
            System.out.println("Venta encontrada: " + venta.getNumeroComprobante());
            System.out.println("Tipo: " + venta.getTipoComprobante());
            System.out.println("Total: " + venta.getTotal());
            System.out.println("Cantidad de detalles: " + venta.getDetalles().size());
            
            ByteArrayOutputStream outputStream = pdfGenerator.generarComprobante(venta);
            byte[] pdfBytes = outputStream.toByteArray();
            
            System.out.println("PDF generado correctamente. Tamaño: " + pdfBytes.length + " bytes");
            
            // Marcar como generado
            String rutaPdf = "comprobantes/" + venta.getNumeroComprobante() + ".pdf";
            ventaService.marcarPdfGenerado(ventaId, rutaPdf);
            
            return pdfBytes;
            
        } catch (Exception e) {
            System.err.println("=== ERROR AL GENERAR PDF ===");
            System.err.println("Tipo de error: " + e.getClass().getName());
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
    }
}