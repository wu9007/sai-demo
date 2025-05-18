package org.chuan.sai.navigate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chuan.sai.MedPromptProvider;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.stereotype.Service;

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

    @Override
    public NavigationResultDto navigate(String symptoms) {
        try {
            //TODO 从数据库查询报告指标
            String indicatorJson = "TODO";
            String prompt = promptProvider.navigatePrompt(symptoms, indicatorJson);

            String resultText = chatClient.prompt()
                    .user(prompt)
                    .advisors(new SimpleLoggerAdvisor())
                    .call().content().trim();
            log.debug("AI 就医导航结果：\n{}", resultText);

            JsonNode jsonNode = objectMapper.readTree(resultText);
            NavigationResultDto result = new NavigationResultDto();
            result.setRecommendedDepartment(jsonNode.path("推荐科室").asText());
            result.setPreliminaryAssessment(jsonNode.path("初步判断").asText());
            result.setSuggestion(jsonNode.path("就医建议").asText());
            result.setRequiredDocuments(jsonNode.path("就医前需要携带的资料").asText());

            return result;
        } catch (Exception e) {
            log.error("就医导航失败", e);
            throw new RuntimeException("生成就医建议失败，请稍后再试");
        }
    }
}
