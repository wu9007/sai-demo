package org.chuan.sai.reportinterpret;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author chuan
 * @version 1.0
 * @since 2025/5/17
 */
@Data
@Builder
public class MedReportInfoDto implements Serializable {

    private String medReportId;

    private Map<String, Object> indicator;

    private List<String> symptom;

    private LocalDateTime createTime;

    private LocalDateTime alterTime;

    private String filePath;
}
