package org.chuan.sai.reportinterpret;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequestMapping("/report-interpret")
@RequiredArgsConstructor
public class InterpretController {

    private final MedReportInfoService medReportInfoService;

    @PostMapping("/extract")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> result = medReportInfoService.extract(file);
            return ResponseEntity.ok(result);
        } catch (Exception exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> save(@RequestBody Map<String, Object> interpret) {
        medReportInfoService.confirm(interpret);
        return ResponseEntity.ok(interpret);
    }
}
