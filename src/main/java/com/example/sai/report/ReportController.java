package com.example.sai.report;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author chuan
 * @version 1.0
 * @since 2025/5/12
 */
@RestController
@RequestMapping("/report")
public class ReportController {

    private final PdfReportParser pdfParser;
    private final MedicalReportExtractor reportExtractor;

    public ReportController(PdfReportParser pdfParser, MedicalReportExtractor reportExtractor) {
        this.pdfParser = pdfParser;
        this.reportExtractor = reportExtractor;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        String text = pdfParser.parse(file);

        if (!reportExtractor.isMedicalReport(text)) {
            return ResponseEntity.ok(Map.of("isMedicalReport", false));
        }

        Map<String, Object> result = reportExtractor.extractIndicators(text);
        //TODO 危急值判断

        return ResponseEntity.ok(result);
    }
}
