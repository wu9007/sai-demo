package org.chuan.sai.reportinterpret;

import org.mapstruct.Mapper;

/**
 * @author chuan
 * @version 1.0
 * @since 2025/5/17
 */
@Mapper(componentModel = "spring")
public interface MedReportInfoConverter {

    /**
     * 转换
     *
     * @param entity entity
     * @return do
     */
    MedReportInfoDto toDto(MedReportInfoDo entity);

    /**
     * 转换
     *
     * @param dto dto
     * @return entity
     */
    MedReportInfoDo toEntity(MedReportInfoDto dto);
}
