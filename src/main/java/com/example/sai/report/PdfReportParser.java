package com.example.sai.report;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author chuan
 * @version 1.0
 * @since 2025/5/12
 */
@Component
public class PdfReportParser {

    public String parse(MultipartFile file) {
        try {
            PagePdfDocumentReader reader = new PagePdfDocumentReader(file.getResource());
            return reader.get().stream()
                    .map(Document::getText)
                    .map(PdfReportParser::clean)
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("PDF 解析失败: " + e.getMessage(), e);
        }
    }

    private static String clean(String rawText) {
        return Arrays.stream(rawText.split("\n"))
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .map(line -> line.replaceAll("\\s{2,}", " "))
                .map(line -> line.replaceAll("[^\\x20-\\x7E\\u4e00-\\u9fa5:.×0-9gGLl/]", ""))
                .collect(Collectors.joining("\n"));
    }
}

