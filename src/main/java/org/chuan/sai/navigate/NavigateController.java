package org.chuan.sai.navigate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chuan
 * @version 1.0
 * @since 2025/5/18
 */
@RequiredArgsConstructor
@RequestMapping("/navigate")
@RestController
public class NavigateController {

    @GetMapping
    public ResponseEntity<?> navigate(@RequestParam String symptom) {
        return ResponseEntity.ok("TODO");
    }
}
