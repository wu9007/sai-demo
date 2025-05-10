package com.example.sai.model_controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author chuan
 * @version 1.0
 * @since 2025/5/8
 */
@RestController
@RequestMapping("/cm")
class ChatModelController {

    private final ChatClient chatClient;

    public ChatModelController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 流式输出问答结果
     */
    @GetMapping(path = "/ai/{userInput}", produces = "text/html;charset=UTF-8")
    Flux<String> generation(@PathVariable String userInput) {
        return this.chatClient.prompt()
                .user(userInput)
                // 动态切换模型
                .options(OllamaOptions.builder().model("qwen3:4b").build())
//                .options(OllamaOptions.builder().model("gemma3:4b").build())
                .stream()
                .content();
    }

//    @GetMapping("/ai")
//    ChatResponse generation(String userInput) {
//        return this.chatClient.prompt()
//                .user(userInput)
//                .call()
//                .chatResponse();
//    }

//    @GetMapping("/ai")
//    ActorFilms generation(String userInput) {
//        String structuredPrompt = """
//        请根据以下要求严格返回JSON数据：
//        1. 格式必须为：{"actor":"演员姓名", "movies":["电影1","电影2"]}
//        2. 不要包含任何额外描述或Schema
//        3. 用户输入：%s
//        """.formatted(userInput);
//
//        return this.chatClient.prompt()
//                .user(structuredPrompt)
//                .call()
//                .entity(ActorFilms.class);
//    }
//    record ActorFilms(String actor, List<String> movies) {}
}
