package org.chuan.sai.navigate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chuan.sai.MedPromptProvider;
import org.chuan.sai.reportinterpret.MedReportInfoMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author chuan
 * @version 1.0
 * @since 2025/5/18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MedicalNavigationServiceImpl implements MedicalNavigationService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final MedPromptProvider promptProvider;

    private final MedReportInfoMapper medReportInfoMapper;

    @Override
    public List<String> getSymptoms() {
        return medReportInfoMapper.getSymptoms();
    }

    @Override
    public NavigationResultDto navigate(String symptoms) {
        try {
            Map<String, Map<String, Object>> indicator = medReportInfoMapper.getIndicator();
            String indicatorJson = objectMapper.writeValueAsString(indicator);
            String prompt = promptProvider.navigatePrompt(symptoms, indicatorJson);

            String answer = chatClient.prompt()
                    .user(prompt)
                    .advisors(new SimpleLoggerAdvisor())
                    .call().content().trim();

            if (answer.startsWith("```json")) {
                answer = answer.replaceFirst("(?s)```json\\s*", "").replaceFirst("```\\s*$", "");
            }

            return objectMapper.readValue(answer, NavigationResultDto.class);
        } catch (Exception e) {
            log.error("就医导航失败", e);
            throw new RuntimeException("生成就医建议失败，请稍后再试");
        }
    }
}
