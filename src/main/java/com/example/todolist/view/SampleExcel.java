package com.example.todolist.view;

import java.net.URLEncoder;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SampleExcel extends AbstractXlsxView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String currentTime = ((java.util.Date) model.get("currentTime")).toString();
        String fileName = (String) model.get("fileName");
        response.setHeader("Content-Disposition",
                "attachement;filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"");
        CellStyle pattern_borderAll_fontSize12_center_bold = workbook.createCellStyle();
        pattern_borderAll_fontSize12_center_bold.setFillPattern(FillPatternType.LESS_DOTS);
        pattern_borderAll_fontSize12_center_bold.setFillPattern(FillPatternType.LESS_DOTS);
        pattern_borderAll_fontSize12_center_bold.setBorderTop(BorderStyle.HAIR);
        pattern_borderAll_fontSize12_center_bold.setBorderRight(BorderStyle.HAIR);
        pattern_borderAll_fontSize12_center_bold.setBorderBottom(BorderStyle.HAIR);
        pattern_borderAll_fontSize12_center_bold.setBorderLeft(BorderStyle.HAIR);
        pattern_borderAll_fontSize12_center_bold.setAlignment(HorizontalAlignment.CENTER);

        Font font12_bold = workbook.createFont();
        font12_bold.setFontName("MS Pゴシック");
        font12_bold.setFontHeightInPoints((short) 12);
        font12_bold.setBold(true);

        pattern_borderAll_fontSize12_center_bold.setFont(font12_bold);

        // ③罫線 上右下左 + フォント12
        CellStyle borderAll_fontSize12 = workbook.createCellStyle();
        borderAll_fontSize12.setBorderTop(BorderStyle.HAIR);
        borderAll_fontSize12.setBorderRight(BorderStyle.HAIR);
        borderAll_fontSize12.setBorderBottom(BorderStyle.HAIR);
        borderAll_fontSize12.setBorderLeft(BorderStyle.HAIR);

        Font font12 = workbook.createFont();
        font12.setFontName("MS Pゴシック");
        font12.setFontHeightInPoints((short) 12);
        borderAll_fontSize12.setFont(font12);

        // ④シート作成
        Sheet sheet = workbook.createSheet("Sample");
        // 列幅設定
        sheet.setColumnWidth(1, 256 * 40);

        // ⑤データセット
        // 2行目を作成(行番号は0から始まる)
        Row row = sheet.createRow(1);
        // 2行目にB列を作成(列番号は0→A列、1→B列、...)
        Cell cell = row.createCell(1);
        // B2に文字列と書式をセット
        cell.setCellValue("currentTime");
        cell.setCellStyle(pattern_borderAll_fontSize12_center_bold);

        // C2に時刻と書式をセット
        row = sheet.createRow(2);
        cell = row.createCell(1);
        cell.setCellValue(currentTime);
        cell.setCellStyle(borderAll_fontSize12);

    }
}
