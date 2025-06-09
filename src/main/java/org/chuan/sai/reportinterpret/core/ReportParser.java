package org.chuan.sai.reportinterpret.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author chuan
 * @version 1.0
 * @since 2025/5/12
 */
@Slf4j
@Component
public class ReportParser {

    private final ChatClient chatClient;

    public ReportParser(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultOptions(OllamaOptions.builder().model("minicpm-v:8b").build())
                .build();
    }

    public String parse(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            log.info("parse medical report file, type = {}", contentType);

            if ("application/pdf".equals(contentType)) {
                return parsePdf(file);
            } else if (contentType.startsWith("image/")) {
                return parseImage(file);
            } else {
                throw new IllegalArgumentException("暂不支持的文件类型: " + contentType);
            }

        } catch (Exception e) {
            log.error("解析报告失败", e);
            throw new RuntimeException("报告解析失败: " + e.getMessage(), e);
        }
    }

    private String parsePdf(MultipartFile file) {
        PagePdfDocumentReader reader = new PagePdfDocumentReader(file.getResource());
        return reader.get().stream()
                .map(Document::getText)
                .map(ReportParser::clean)
                .collect(Collectors.joining("\n"));
    }

    private String parseImage(MultipartFile file) {
        String prompt = """
        请提取图片上的所有文字。图片中的文字可能有不同的格式、大小和字体，请确保识别每一行文字，并以结构化的方式输出。
        提取的文本需要按原格式返回，不要添加其他信息或对格式进行修改。请忽略图片中的图像或非文本部分。
        """;

        MimeType mimeType = MimeTypeUtils.parseMimeType(file.getContentType());
        String response = chatClient.prompt()
                .user((u -> {
                    u.text(prompt).media(mimeType, file.getResource());
                }))
                .advisors(new SimpleLoggerAdvisor())
                .call().content().trim();
        return clean(response);
    }

    private static String clean(String rawText) {
        return Arrays.stream(rawText.split("\n"))
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .map(line -> line.replaceAll("\\s{2,}", " "))
                .map(line -> line.replaceAll("[^\\p{IsHan}\\p{IsAlphabetic}\\p{IsDigit}\\s.:×/%mgGLl-]", ""))
                .collect(Collectors.joining("\n"));
    }
}


