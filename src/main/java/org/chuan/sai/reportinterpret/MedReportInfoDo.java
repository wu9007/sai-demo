package org.chuan.sai.reportinterpret;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
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
@TableName("med_report_info")
public class MedReportInfoDo implements Serializable {

    @TableId(type = IdType.ASSIGN_UUID)
    private String medReportId;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Map<String, Object>> indicator;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Map<String, Object>> interpret;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> symptom;

    private LocalDateTime createTime;

    private LocalDateTime alterTime;

    private String filePath;
}
