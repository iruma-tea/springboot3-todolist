package com.example.todolist.view;

import java.util.Map;

import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SamplePdf extends AbstractPdfView {

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        String currentTime = ((java.util.Date) model.get("currentTime")).toString();
        document.add(new Paragraph(currentTime));

        Table table = new Table(1);
        table.addCell("currentTime");
        table.addCell(currentTime);
        document.add(table);
    }
}