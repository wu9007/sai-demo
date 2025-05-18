package org.chuan.sai.reportinterpret;

import lombok.RequiredArgsConstructor;
import org.chuan.sai.reportinterpret.core.IndicatorExtractor;
import org.chuan.sai.reportinterpret.core.ReportParser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public Map<String, Map<String, Object>> confirm(Map<String, Map<String, Object>> indicators) {
        //指标解读
        Map<String, Map<String, Object>> interprets = reportExtractor.indicatorsInterpret(indicators);
        // 提取“相关症状”并去重
        List<String> symptom = interprets.values().stream()
                .map(m -> m.get("相关症状"))
                .filter(Objects::nonNull)
                .flatMap(v -> v instanceof Collection ? ((Collection<?>) v).stream() : Stream.of(v))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .distinct()
                .collect(Collectors.toList());
        //构件对象
        MedReportInfoDto reportInfoDto = MedReportInfoDto.builder()
                .indicator(indicators)
                .interpret(interprets)
                .symptom(symptom)
                .filePath("D:/reports/")
                .createTime(LocalDateTime.now())
                .alterTime(LocalDateTime.now())
                .build();
        MedReportInfoDo medReportInfoDo = medReportInfoConverter.toEntity(reportInfoDto);
        //更新或保存
        if (StringUtils.hasLength(medReportInfoDo.getMedReportId())) {
            medReportInfoMapper.updateById(medReportInfoDo);
        } else {
            medReportInfoMapper.insert(medReportInfoDo);
        }
        //合并
        Map<String, Map<String, Object>> result = new HashMap<>(indicators);
        interprets.forEach((key, value) -> {
            Map<String, Object> indicator = result.get(key);
            indicator.putAll(value);
        });
        return result;
    }
}
