package com.example.sai.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chuan
 * @version 1.0
 * @since 2025/5/12
 */
@RestController
@RequestMapping("/report")
public class ReportController {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public ReportController(ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder.defaultOptions(OllamaOptions.builder().model("gemma3:4b").build()).build();
        this.objectMapper = objectMapper;
    }

    @GetMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadReport(@RequestParam("file") MultipartFile file) throws JsonProcessingException {
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(file.getResource());
        List<Document> documents = pdfReader.get();
        String text = documents.stream().map(Document::getText).collect(Collectors.joining());
        System.out.println(text);
        String prompt = """
                你是医学报告字段抽取助手，请从以下内容中提取结构化字段并返回JSON：
                            
                【内容】
                %s

                【输出JSON】
                {
                  "Hb": "数值",
                  "WBC": "数值",
                  "PLT": "数值"
                }
                """.formatted(text);

        String content = this.chatClient
                .prompt().user(prompt)
                .call().content();

        Map<String, Object> result = objectMapper.readValue(content, new TypeReference<>() {
        });
        return ResponseEntity.ok(result);
    }
}
