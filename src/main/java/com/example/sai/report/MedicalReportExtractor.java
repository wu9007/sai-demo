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

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public MedicalReportExtractor(ChatClient.Builder builder, ObjectMapper objectMapper) {
        this.chatClient = builder.defaultOptions(OllamaOptions.builder().model("gemma3:4b").build()).build();
        this.objectMapper = objectMapper;
    }

    public boolean isMedicalReport(String text) {
        String prompt = "你将收到一段文本，请判断它是否属于医学检验或检查类报告（如血常规、生化、心电图、CT等），如果是，回答“Y”；否则回答“N”。\n文本内容：";
        String result = chatClient.prompt().user(prompt + "\n" + text).call().content().trim();
        return result.startsWith("Y");
    }

    public Map<String, Object> extractIndicators(String text) {
        String prompt = """
                你是一名医学NLP助手。请从以下医学报告文本中提取所有关键检测指标，并判断是否异常。返回一个 JSON 对象，格式如下（不要包含 ```json 标记）：
                                
                {
                  "指标名称1": {
                    "值": "xxx",
                    "单位": "xxx",
                    "是否异常": true/false,
                    "参考范围": "x - y 单位",
                    "临床解释": "请使用通俗易懂的语言解释该指标的意义，如果该值在正常范围内，直接说明该指标正常；如果异常，请解释可能的临床意义和可能的疾病风险或健康问题。"
                  },
                  ...
                }
                                
                要求：
                - 所有字段必须完整，包括值、单位、是否异常、参考范围和临床解释；
                - 若参考范围未在原文中出现，请补充常见范围；
                - 对于正常指标，临床解释应简明扼要地指出该指标在正常范围内，避免过度阐释；
                - 对于异常指标，解释应该简洁而温和，避免过度忧虑，给出合理的健康建议，避免使用强烈的语气，建议进一步检查而非恐慌性提示；
                - 仅处理与检验结果相关的字段，忽略非医学指标；
                - 保留原始单位，不换算；
                                
                以下是报告原文：
                ---
                %s
                ---
                """.formatted(text);
        String answer = chatClient.prompt().user(prompt).call().content().trim();

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

