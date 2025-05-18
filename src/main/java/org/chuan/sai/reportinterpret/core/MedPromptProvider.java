package org.chuan.sai.reportinterpret.core;

import org.springframework.stereotype.Component;

/**
 * @author chuan
 * @version 1.0
 * @since 2025/5/18
 */
@Component
public class MedPromptProvider {

    /**
     * 判断是否为医学报告的提示语
     */
    public String isMedicalReportPrompt(String text) {
        return """
                你将收到一段文本，请判断它是否属于医学检验或检查类报告（如血常规、生化、心电图、CT等），如果是，回答“Y”；否则回答“N”。
                文本内容：
                %s
                """.formatted(text);
    }

    /**
     * 指标提取提示语
     */
    public String extractIndicatorPrompt(String text) {
        return """
                你是一名医学 NLP 专家，请从以下医学检验报告中提取所有医学指标，并结构化输出为标准 JSON，格式如下：

                {
                  "指标名称1": {
                    "值": "xxx",
                    "单位": "xxx",
                    "是否异常": true/false,
                    "参考范围": "x - y 单位"
                  },
                  ...
                }

                提取与分析要求如下：
                - 项目名必须与原文一致，不替换不概括；
                - 若原文缺少参考范围，可补充常见医学标准；
                - 所有单位保留原文，不换算；
                - 忽略非医学指标、非结构化或不清楚的内容。

                以下是医学报告原文：
                ---
                %s
                ---
                """.formatted(text);
    }

    /**
     * 指标解读 Prompt：根据结构化指标进行详细解释
     */
    /**
     * 指标解读 Prompt：根据结构化指标进行详细解释，并提取可能出现的症状
     */
    public String interpretIndicatorsPrompt(String indicatorJson) {
        return """
                你是一名医学解释专家，请根据以下结构化医学指标，为患者和医生逐项生成详细解读。每个指标包含以下内容：

                - 当前值是否正常？（提示轻度/明显异常）；
                - 临床解释：通俗语言解释结果是否正常、可能的日常原因（如熬夜、饮酒、药物等）、是否需要就医、是否需复查，避免引发恐慌；
                - 专业解释：逐步推理说明该指标的生理功能 → 来源 → 升高/降低的生理与病理机制 → 常见疾病关联 → 判断严重性与后续检查建议等；
                - 可能出现的症状：列出与该指标异常相关的常见临床症状，如乏力、头晕、恶心、黄疸等，用数组形式表示；
                - 输出必须为结构化 JSON 格式，示例如下：

                {
                  "指标名称1": {
                    "临床解释": "……",
                    "专业解释": "……",
                    "相关症状": ["头晕", "乏力"]
                  },
                  ...
                }

                要求：
                - 不要遗漏任何结构字段；
                - 若无症状相关性，请返回空数组 "相关症状": []；
                - 使用中文简洁描述症状。

                以下是结构化医学指标：
                ---
                %s
                ---
                """.formatted(indicatorJson);
    }
}

