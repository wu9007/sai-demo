package com.example.sai.report;

import lombok.SneakyThrows;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;

/**
 * 通过Advisor加入危急值提示语
 *
 * @author chuan
 * @version 1.0
 * @since 2025/5/13
 */
public class CriticalValueAdvisor implements CallAroundAdvisor {

    //TODO 从数据库中获取
    private final static String criticalValuePrompt = """
             【危急值规则】：
                            - ALT > 50 U/L
                            - AST > 500 U/L
                            - Hb < 60 g/L
                            - PLT < 20 ×10^9/L
                            - TP < 60 g/L
            """;

    @Override
    public String getName() {
        return "critical_value_advisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @SneakyThrows
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest request, CallAroundAdvisorChain chain) {
        AdvisedRequest advisedRequest = AdvisedRequest.from(request)
                .systemText("你是一名医学 NLP 助手，请严格按照以下危急值规则判断指标是否为危急值：\n" + criticalValuePrompt)
                .userText(request.userText())
                .build();

        return chain.nextAroundCall(advisedRequest);
    }
}


