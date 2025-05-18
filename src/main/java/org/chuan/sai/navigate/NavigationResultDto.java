package org.chuan.sai.navigate;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chuan
 * @version 1.0
 * @since 2025/5/18
 */
@Data
public class NavigationResultDto implements Serializable {
    private String recommendedDepartment;
    private String preliminaryAssessment;
    private String suggestion;
    private String requiredDocuments;
}
