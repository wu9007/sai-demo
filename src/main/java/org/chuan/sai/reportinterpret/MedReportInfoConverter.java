package org.chuan.sai.reportinterpret;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author chuan
 * @version 1.0
 * @since 2025/5/17
 */
@Mapper(componentModel = "spring")
public interface MedReportInfoConverter {

    ObjectMapper MAPPER = new ObjectMapper();

    @Mappings({
            @Mapping(source = "indicator", target = "indicator", qualifiedByName = "stringToMap"),
            @Mapping(source = "symptom", target = "symptom", qualifiedByName = "stringToList")
    })
    MedReportInfoDto toDto(MedReportInfoDo entity);

    @Mappings({
            @Mapping(source = "indicator", target = "indicator", qualifiedByName = "mapToString"),
            @Mapping(source = "symptom", target = "symptom", qualifiedByName = "listToString")
    })
    MedReportInfoDo toEntity(MedReportInfoDto dto);

    @Named("stringToMap")
    default Map<String, Map<String, Object>> stringToMap(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            return MAPPER.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse indicator", e);
        }
    }

    @Named("mapToString")
    default String mapToString(Map<String, Map<String, Object>> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(map);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize indicator", e);
        }
    }

    @Named("stringToList")
    default List<String> stringToList(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return MAPPER.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse symptom", e);
        }
    }

    @Named("listToString")
    default String listToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(list);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize symptom", e);
        }
    }
}
