package org.chuan.sai.reportinterpret;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author chuan
 * @version 1.0
 * @since 2025/5/17
 */
public interface MedReportInfoService {

    Map<String, Object> extract(MultipartFile file);

    void confirm(MultipartFile file, @RequestBody Map<String, Object> interpret);
}
