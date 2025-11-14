package com.healthtracker.helper;

import com.healthtracker.dto.HealthRecordDto;
import com.healthtracker.model.HealthRecord;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class PdfExportHelper {

    public byte[] generatePdf(List<HealthRecordDto> records) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Health Records Export"));

        if (!records.isEmpty()) {
            Table table = new Table(6); // Columns: Date, BP, Sugar, Weight, Heart Rate, Notes (adjusted for nulls)
            table.addHeaderCell("Date");
            table.addHeaderCell("BP");
            table.addHeaderCell("Sugar");
            table.addHeaderCell("Weight");
            table.addHeaderCell("Heart Rate");
            table.addHeaderCell("Notes");

            for (HealthRecordDto r : records) {
                // Null-safe cells
                table.addCell(r.getDate() != null ? r.getDate().toString() : "N/A");
                Integer diastolic = r.getBpDiastolic() != null ? r.getBpDiastolic() : 0;
                table.addCell(r.getBpSystolic() + "/" + diastolic);
                table.addCell(r.getSugarLevel() != null ? r.getSugarLevel().toString() : "N/A");
                table.addCell(r.getWeight() != null ? r.getWeight().toString() : "N/A");
                table.addCell(r.getHeartRate() != null ? r.getHeartRate().toString() : "N/A");
                table.addCell(r.getNotes() != null ? r.getNotes() : "N/A");
            }
            document.add(table);
        } else {
            document.add(new Paragraph("No records found."));
        }

        document.close();
        return baos.toByteArray();
    }
}