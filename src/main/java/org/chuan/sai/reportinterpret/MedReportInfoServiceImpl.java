package org.chuan.sai.reportinterpret;

import lombok.RequiredArgsConstructor;
import org.chuan.sai.reportinterpret.core.IndicatorExtractor;
import org.chuan.sai.reportinterpret.core.ReportParser;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author chuan
 * @version 1.0
 * @since 2025/5/17
 */
@Service
@RequiredArgsConstructor
public class MedReportInfoServiceImpl implements MedReportInfoService {

    private final MedReportInfoMapper medReportInfoMapper;
    private final MedReportInfoConverter medReportInfoConverter;
    private final ReportParser reportParser;
    private final IndicatorExtractor reportExtractor;

    @Override
    public Map<String, Object> extract(MultipartFile file) {
        String text = reportParser.parse(file);
        if (!reportExtractor.isMedicalReport(text)) {
            throw new IllegalArgumentException("is not medical report file");
        }
        try {
            // 文件保存
            String baseDir = "D:/reports/";
            File dir = new File(baseDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String filePath = baseDir + UUID.randomUUID() + "_" + file.getOriginalFilename();
            file.transferTo(new File(filePath));
        } catch (Exception e) {
            throw new RuntimeException("原始文件保存失败");
        }
        return reportExtractor.extractIndicators(text);
    }

    @Override
    public void confirm(Map<String, Map<String, Object>> indicators) {
        //指标解读
        Map<String, Map<String, Object>> interpret = reportExtractor.indicatorsInterpret(indicators);
        for (Map.Entry<String, Map<String, Object>> item : interpret.entrySet()) {
            String key = item.getKey();
            Map<String, Object> value = item.getValue();
            Map<String, Object> indicator = indicators.get(key);
            value.putAll(indicator);
        }
        //TODO 提取解读中的“相关症状”
        List<String> symptom = new ArrayList<>();
        MedReportInfoDto reportInfoDto = MedReportInfoDto.builder()
                .indicator(interpret)
                .symptom(symptom)
                .filePath("D:/reports/")
                .createTime(LocalDateTime.now())
                .alterTime(LocalDateTime.now())
                .build();
        MedReportInfoDo medReportInfoDo = medReportInfoConverter.toEntity(reportInfoDto);
        if (StringUtils.hasLength(medReportInfoDo.getMedReportId())) {
            medReportInfoMapper.updateById(medReportInfoDo);
        } else {
            medReportInfoMapper.insert(medReportInfoDo);
        }
    }
}
