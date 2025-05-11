package com.example.sai;

import com.example.sai.tool.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author chuan
 */
@SpringBootApplication
public class SaiDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaiDemoApplication.class, args);
    }

    @Bean
    ChatClient chatClient(ChatMemory chatMemory, ChatClient.Builder chatClientBuilder, ToolCallbackProvider toolCallbackProvider) {
        MessageChatMemoryAdvisor chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        return chatClientBuilder
                // chat memory
                .defaultAdvisors(chatMemoryAdvisor)
                // 设置MCP调用工具【使用工具流式输出将会失效】
                .defaultToolCallbacks(toolCallbackProvider)
                // 设置默认调用工具【使用工具流式输出将会失效】
                .defaultTools(new DateTimeTools())
                .build();
    }
}
