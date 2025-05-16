package com.example.sai.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class MedicalReportController {

    private final MedicalReportParser medicalReportParser;
    private final MedicalReportExtractor reportExtractor;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        String text = medicalReportParser.parse(file);
        if (!reportExtractor.isMedicalReport(text)) {
            return ResponseEntity.ok(Map.of("isMedicalReport", false));
        }

        Map<String, Object> result = reportExtractor.extractIndicators(text);

        return ResponseEntity.ok(result);
    }
}
