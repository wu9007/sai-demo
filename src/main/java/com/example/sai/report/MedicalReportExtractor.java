package com.example.sai.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author chuan
 * @version 1.0
 * @since 2025/5/12
 */
@Component
public class MedicalReportExtractor {

    private final ChatClient medicalChatClient;
    private final ObjectMapper objectMapper;

    public MedicalReportExtractor(ChatClient.Builder builder, ChatClient chatClient, ObjectMapper objectMapper) {
        this.medicalChatClient = builder
                .defaultOptions(OllamaOptions.builder().model("gemma3:4b").build())
                .build();
        this.objectMapper = objectMapper;
    }

    public boolean isMedicalReport(String text) {
        String prompt = "你将收到一段文本，请判断它是否属于医学检验或检查类报告（如血常规、生化、心电图、CT等），如果是，回答“Y”；否则回答“N”。\n文本内容：";
        String result = medicalChatClient.prompt().user(prompt + "\n" + text).call().content().trim();
        return result.startsWith("Y");
    }

    public Map<String, Object> extractIndicators(String text) {
        String prompt = """
                你是一名专业医学 NLP 助手，请从以下医学检验报告文本中提取关键检测指标，并进行结构化分析与解读以及危急值判断，返回一个标准 JSON 对象，格式如下：

                {
                  "指标名称1": {
                    "值": "xxx",
                    "单位": "xxx",
                    "是否异常": true/false,
                    "参考范围": "x - y 单位",
                    "危急值判断": "危急值/正常/无标准",
                    "临床解释": "……",
                    "专业解释": "……"
                  },
                  ...
                }

                要求如下：
                1. 完整提取所有与检验结果相关的医学指标（如肝功能、肾功能、血常规等）；
                2. 对于每个指标，字段必须完整：值、单位、是否异常、参考范围、临床解释；
                3. 如果参考范围原文未标明，请补充常见医学标准范围（注明单位）；
                4. 临床解释要使用通俗语言解释结果是否正常、可能的日常原因（如熬夜、饮酒、药物等）、是否需要就医、是否需复查等，避免引发恐慌；
                5. 专业解释要使用逐步推理层层深入的方式讲解该指标的生理功能 → 来源 → 升高/降低的生理与病理机制 → 常见疾病关联 → 判断严重性与后续检查建议等。可包含酶学、器官功能、生理代谢等内容；
                5. 若数值正常，说明“在正常范围内，提示功能良好”；
                6. 若数值轻度异常（如略高或略低），请注明“轻度”并说明可能的生活因素及非疾病性原因；
                7. 若异常较为明显，请指出其临床指向性，但仍避免恐慌性措辞；
                8. 所有单位请保留原文单位，不做换算；
                9. 忽略非医学指标、不清晰或非结构化的内容；

                以下是医学报告原文：
                ---
                %s
                ---
                """.formatted(text);
        String answer = medicalChatClient.prompt()
                .user(prompt)
                .advisors(new CriticalValueAdvisor())
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

