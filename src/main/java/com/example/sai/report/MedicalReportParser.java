package com.example.sai.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
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
@RequiredArgsConstructor
public class MedicalReportParser {

    private final ChatClient chatClient;

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
                .map(MedicalReportParser::clean)
                .collect(Collectors.joining("\n"));
    }

    private String parseImage(MultipartFile file) {
        String prompt = """
                你现在看到的是一张医学检验报告的照片，请从中提取出完整的表格信息，包括项目名称、检测值、单位、参考范围等。

                请按以下格式提取清单：
                项目名称 检测值 单位 参考范围

                如：
                白细胞计数 5.8 ×10^9/L 3.5-9.5 ×10^9/L

                要求：
                - 保留医学项目名称，不要省略；
                - 不要仅提取数值，要还原出完整的表头和字段内容；
                - 如果存在图片中无法识别的内容，用“无法识别”代替；
                - 如果表格中项目名、值、单位、范围是多行格式，请组合为一行输出；
                - 保持换行分项。
                """;

        MimeType mimeType = MimeTypeUtils.parseMimeType(file.getContentType());
        String response = chatClient.prompt().user((u -> {
            u.text(prompt).media(mimeType, file.getResource());
        })).call().content().trim();
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


