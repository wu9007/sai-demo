package org.chuan.sai.reportinterpret.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chuan.sai.MedPromptProvider;
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
public class IndicatorExtractor {

    private final ChatClient medicalChatClient;
    private final MedPromptProvider medPromptProvider;
    private final ObjectMapper objectMapper;

    public boolean isMedicalReport(String text) {
        log.info("check medical report.");
        String result = medicalChatClient.prompt()
                .user(medPromptProvider.isMedicalReportPrompt(text))
                .advisors(new SimpleLoggerAdvisor())
                .call().content().trim();
        return result.startsWith("Y");
    }

    /**
     * 指标提取
     *
     * @param text 原文
     * @return 指标
     */
    public Map<String, Object> extractIndicators(String text) {
        log.info("extract indicators.");
        String answer = medicalChatClient.prompt()
                .user(medPromptProvider.extractIndicatorPrompt(text))
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

    /**
     * 指标解读
     *
     * @param indicators 指标
     * @return 解读
     */
    public Map<String, Map<String, Object>> indicatorsInterpret(Map<String, Map<String, Object>> indicators) {
        log.info("interpret indicators.");
        String indicatorJson;
        try {
            indicatorJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(indicators);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("指标转 JSON 失败", e);
        }

        String prompt = medPromptProvider.interpretIndicatorsPrompt(indicatorJson);

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
            throw new RuntimeException("解读 JSON 解析失败", e);
        }
    }
}

