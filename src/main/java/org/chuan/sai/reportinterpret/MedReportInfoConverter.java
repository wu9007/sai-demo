package org.chuan.sai.reportinterpret;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author chuan
 * @version 1.0
 * @since 2025/5/17
 */
@Mapper(componentModel = "spring")
public interface MedReportInfoConverter {

    MedReportInfoConverter INSTANCE = Mappers.getMapper(MedReportInfoConverter.class);

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
