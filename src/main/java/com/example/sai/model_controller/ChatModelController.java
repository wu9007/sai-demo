package com.example.sai.model_controller;

import com.example.sai.tool.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * @author chuan
 * @version 1.0
 * @since 2025/5/8
 */
@RestController
@RequestMapping("/chat")
class ChatModelController {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final VectorStore vectorStore;

    public ChatModelController(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory, VectorStore vectorStore) {
        MessageChatMemoryAdvisor chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        this.chatClient = chatClientBuilder
                // chat memory
                .defaultAdvisors(chatMemoryAdvisor)
                // 设置默认调用工具
//                .defaultTools(new DateTimeTools())
                .build();
        this.chatMemory = chatMemory;
        this.vectorStore = vectorStore;
    }

    /**
     * 流式输出问答结果
     */
    @GetMapping(path = "/{chatId}/{userInput}", produces = "text/html;charset=UTF-8")
    Flux<String> generation(@PathVariable String chatId, @PathVariable String userInput) {
        var qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder().similarityThreshold(0.65d).topK(6).build())
                .build();

        return this.chatClient.prompt().user(userInput)
                .advisors(advisorSpec -> advisorSpec
                        // chat memory param
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100)
                        // response from the vector database appended to the user text to provide context for the AI model
                        .advisors(qaAdvisor)
                )
                // 动态切换模型
                .options(OllamaOptions.builder().model("qwen3:4b").build())
                // 设置调用工具【使用工具流式输出将会失效】
                .tools(new DateTimeTools())
                .stream().content();
    }

    /**
     * 问答记录
     */
    @GetMapping("/history/{chatId}")
    public List<Message> queryChatId(@PathVariable String chatId) {
        return this.chatMemory.get(chatId);
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
