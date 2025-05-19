package org.chuan.sai.reportinterpret;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chuan
 * @version 1.0
 * @since 2025/5/17
 */
@Mapper
public interface MedReportInfoMapper extends BaseMapper<MedReportInfoDo> {

    ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 获取症状
     *
     * @return 症状
     */
    default List<String> getSymptoms() {
        LambdaQueryWrapper<MedReportInfoDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(MedReportInfoDo::getSymptom);
        List<MedReportInfoDo> medReportInfoDos = this.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(medReportInfoDos)) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        try {
            for (MedReportInfoDo medReportInfoDo : medReportInfoDos) {
                String symptom = medReportInfoDo.getSymptom();
                List<String> symptomList = MAPPER.readValue(symptom, new TypeReference<>() {
                });
                result.addAll(symptomList);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse symptom", e);
        }
    }

    /**
     * 获取指标
     *
     * @return 指标
     */
    default Map<String, Map<String, Object>> getIndicator() {
        LambdaQueryWrapper<MedReportInfoDo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(MedReportInfoDo::getIndicator);
        List<MedReportInfoDo> medReportInfoDos = this.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(medReportInfoDos)) {
            return Collections.emptyMap();
        }
        Map<String, Map<String, Object>> result = new HashMap<>();
        try {
            for (MedReportInfoDo medReportInfoDo : medReportInfoDos) {

                String indicator = medReportInfoDo.getIndicator();
                Map<String, Map<String, Object>> indicatorMap = MAPPER.readValue(indicator, new TypeReference<>() {
                });
                result.putAll(indicatorMap);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse indicator", e);
        }
    }
}
