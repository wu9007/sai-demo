package com.example.sai.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author chuan
 * @version 1.0
 * @since 2025/5/12
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MedicalReportExtractor {

    private final ChatClient medicalChatClient;
    private final ObjectMapper objectMapper;

    public boolean isMedicalReport(String text) {
        log.info("check medical report.");
        String prompt = "你将收到一段文本，请判断它是否属于医学检验或检查类报告（如血常规、生化、心电图、CT等），如果是，回答“Y”；否则回答“N”。\n文本内容：";
        String result = medicalChatClient.prompt()
                .user(prompt + "\n" + text)
                .advisors(new SimpleLoggerAdvisor())
                .call().content().trim();
        return result.startsWith("Y");
    }

    public Map<String, Object> extractIndicators(String text) {
        log.info("extract indicators.");
        String prompt = """
                你是一名医学 NLP 专家，请从以下医学检验报告中提取所有医学指标，并结构化输出为标准 JSON，格式如下：

                {
                  "指标名称1": {
                    "值": "xxx",
                    "单位": "xxx",
                    "是否异常": true/false,
                    "参考范围": "x - y 单位",
                    "临床解释": "……",
                    "专业解释": "……"
                  },
                  ...
                }

                提取与分析要求如下：
                1. 项目名必须与原文一致，不替换不概括；
                2. 若原文缺少参考范围，可补充常见医学标准；
                3. 临床解释要使用通俗语言解释结果是否正常、可能的日常原因（如熬夜、饮酒、药物等）、是否需要就医、是否需复查等，避免引发恐慌；
                4. 专业解释要使用逐步推理层层深入的方式讲解该指标的生理功能 → 来源 → 升高/降低的生理与病理机制 → 常见疾病关联 → 判断严重性与后续检查建议等。可包含酶学、器官功能、生理代谢等内容；
                5. 数值若正常：说明“在正常范围，提示功能良好”；
                6. 异常值请标明“轻度”或“明显”，结合生活与病因分析，避免恐慌性措辞；
                7. 所有单位保留原文，不换算；
                8. 忽略非医学指标、非结构化或不清楚的内容。

                以下是医学报告原文：
                ---
                %s
                ---
                """.formatted(text);

        String answer = medicalChatClient.prompt()
                .user(prompt)
                .advisors(new SimpleLoggerAdvisor())
                .call().content().trim();

        if (answer.startsWith("```json")) {
            answer = answer.replaceFirst("(?s)```json\\s*", "").replaceFirst("```\\s*$", "");
        }

        try {
            return objectMapper.readValue(answer, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 解析失败", e);
        }
    }
}

