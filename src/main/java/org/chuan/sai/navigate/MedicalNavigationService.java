package org.chuan.sai.navigate;

/**
 * @author chuan
 * @version 1.0
 * @since 2025/5/18
 */
public interface MedicalNavigationService {

    /**
     * 根据症状和指标生成就医建议
     *
     * @param symptoms 症状列表逗号分隔
     * @return 就医导航结果
     */
    NavigationResultDto navigate(String symptoms);
}
