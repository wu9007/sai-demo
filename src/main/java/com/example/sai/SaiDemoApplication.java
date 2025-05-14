package com.example.sai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.ollama.api.OllamaOptions;
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
    ChatClient chatClient(ChatMemory chatMemory, ChatClient.Builder chatClientBuilder) {
        MessageChatMemoryAdvisor chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        return chatClientBuilder
                .defaultOptions(OllamaOptions.builder().model("gemma3:4b").build())
                // chat memory
                .defaultAdvisors(chatMemoryAdvisor)
                .build();
    }
}
