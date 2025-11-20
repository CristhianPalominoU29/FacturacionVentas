package com.example.sistemaventas.util;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.example.sistemaventas.model.DetalleVenta;
import com.example.sistemaventas.model.Venta;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Component
public class PdfGenerator {

    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(41, 128, 185);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public ByteArrayOutputStream generarComprobante(Venta venta) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Configurar formato de moneda
            Locale locale = venta.getMoneda().equals("PEN") ? new Locale("es", "PE") : Locale.US;
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);

            // ============ TÍTULO ============
            document.add(new Paragraph(venta.getTipoComprobante())
                    .setFontSize(24)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(HEADER_COLOR));

            document.add(new Paragraph("N° " + venta.getNumeroComprobante())
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("\n"));

            // ============ DATOS DEL EMISOR ============
            document.add(new Paragraph("DATOS DEL EMISOR")
                    .setBold().setFontSize(12).setFontColor(HEADER_COLOR));
            document.add(new Paragraph("Razón Social: MI EMPRESA S.A.C.").setFontSize(10));
            document.add(new Paragraph("RUC: 20123456789").setFontSize(10));
            document.add(new Paragraph("Dirección: Av. Principal 123, Lima, Perú").setFontSize(10));
            document.add(new Paragraph("Teléfono: (01) 123-4567").setFontSize(10));
            document.add(new Paragraph("\n"));

            // ============ DATOS DEL CLIENTE ============
            document.add(new Paragraph("DATOS DEL CLIENTE")
                    .setBold().setFontSize(12).setFontColor(HEADER_COLOR));

            String tipoDoc = venta.getCliente().getTipoDocumento();
            String numeroDoc = venta.getCliente().getNumeroDocumento();
            String nombreCliente;

            if ("RUC".equals(tipoDoc)) {
                nombreCliente = venta.getCliente().getRazonSocial();
                document.add(new Paragraph("RUC: " + numeroDoc).setFontSize(10));
                document.add(new Paragraph("Razón Social: " + nombreCliente).setFontSize(10));
            } else {
                nombreCliente = venta.getCliente().getNombres() + " " +
                        venta.getCliente().getApellidoPaterno() + " " +
                        (venta.getCliente().getApellidoMaterno() != null ? venta.getCliente().getApellidoMaterno() : "");
                document.add(new Paragraph("DNI: " + numeroDoc).setFontSize(10));
                document.add(new Paragraph("Cliente: " + nombreCliente.trim()).setFontSize(10));
            }

            if (venta.getCliente().getDireccion() != null && !venta.getCliente().getDireccion().isBlank()) {
                document.add(new Paragraph("Dirección: " + venta.getCliente().getDireccion()).setFontSize(10));
            }

            // ============ FECHA Y MONEDA (ESTO ARREGLA EL ERROR "HourOfDay") ============
            String fechaFormateada = venta.getFechaVenta().format(DATE_FORMATTER);
            document.add(new Paragraph("Fecha: " + fechaFormateada).setFontSize(10));
            document.add(new Paragraph("Moneda: " + (venta.getMoneda().equals("PEN") ? "Soles (S/)" : "Dólares ($)")).setFontSize(10));
            document.add(new Paragraph("\n"));

            // ============ TABLA DE PRODUCTOS ============
            Table tablaProductos = new Table(UnitValue.createPercentArray(new float[]{1, 4, 2, 2, 2}));
            tablaProductos.setWidth(UnitValue.createPercentValue(100));

            tablaProductos.addHeaderCell(new Paragraph("CANT.").setBold().setFontSize(10)
                    .setBackgroundColor(HEADER_COLOR).setFontColor(ColorConstants.WHITE));
            tablaProductos.addHeaderCell(new Paragraph("DESCRIPCIÓN").setBold().setFontSize(10)
                    .setBackgroundColor(HEADER_COLOR).setFontColor(ColorConstants.WHITE));
            tablaProductos.addHeaderCell(new Paragraph("P. UNIT.").setBold().setFontSize(10)
                    .setBackgroundColor(HEADER_COLOR).setFontColor(ColorConstants.WHITE));
            tablaProductos.addHeaderCell(new Paragraph("SUBTOTAL").setBold().setFontSize(10)
                    .setBackgroundColor(HEADER_COLOR).setFontColor(ColorConstants.WHITE));
            tablaProductos.addHeaderCell(new Paragraph("TOTAL").setBold().setFontSize(10)
                    .setBackgroundColor(HEADER_COLOR).setFontColor(ColorConstants.WHITE));

            for (DetalleVenta detalle : venta.getDetalles()) {
                tablaProductos.addCell(new Paragraph(String.valueOf(detalle.getCantidad()))
                        .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
                tablaProductos.addCell(new Paragraph(detalle.getProducto().getNombre()).setFontSize(9));
                tablaProductos.addCell(new Paragraph(currencyFormat.format(detalle.getPrecioUnitario()))
                        .setFontSize(9).setTextAlignment(TextAlignment.RIGHT));
                tablaProductos.addCell(new Paragraph(currencyFormat.format(detalle.getSubtotal()))
                        .setFontSize(9).setTextAlignment(TextAlignment.RIGHT));
                tablaProductos.addCell(new Paragraph(currencyFormat.format(detalle.getSubtotal()))
                        .setFontSize(9).setTextAlignment(TextAlignment.RIGHT));
            }

            document.add(tablaProductos);
            document.add(new Paragraph("\n"));

            // ============ TOTALES ============
            Table tablaTotales = new Table(UnitValue.createPercentArray(new float[]{3, 1}));
            tablaTotales.setWidth(UnitValue.createPercentValue(100));

            tablaTotales.addCell(new Paragraph("SUBTOTAL:").setBold().setFontSize(11).setTextAlignment(TextAlignment.RIGHT));
            tablaTotales.addCell(new Paragraph(currencyFormat.format(venta.getSubtotal())).setFontSize(11).setTextAlignment(TextAlignment.RIGHT));

            tablaTotales.addCell(new Paragraph("IGV (18%):").setBold().setFontSize(11).setTextAlignment(TextAlignment.RIGHT));
            tablaTotales.addCell(new Paragraph(currencyFormat.format(venta.getIgv())).setFontSize(11).setTextAlignment(TextAlignment.RIGHT));

            tablaTotales.addCell(new Paragraph("TOTAL:").setBold().setFontSize(14)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBackgroundColor(HEADER_COLOR).setFontColor(ColorConstants.WHITE));
            tablaTotales.addCell(new Paragraph(currencyFormat.format(venta.getTotal())).setBold().setFontSize(14)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBackgroundColor(HEADER_COLOR).setFontColor(ColorConstants.WHITE));

            document.add(tablaTotales);

            // ============ PIE DE PÁGINA ============
            document.add(new Paragraph("\n\n"));
            document.add(new Paragraph("Gracias por su compra")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10)
                    .setItalic());

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar el PDF: " + e.getMessage(), e);
        }

        return outputStream;
    }
}