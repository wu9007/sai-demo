package com.example.sai.rag_controller;

import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author chuan
 * @version 1.0
 * @since 2025/5/8
 */
@RestController
@RequestMapping("/rag")
public class RagController {

    private final MyPagePdfDocumentReader reader;
    private final MyTokenTextSplitter tokenTextSplitter;

    public RagController(MyPagePdfDocumentReader reader, MyTokenTextSplitter tokenTextSplitter) {
        this.reader = reader;
        this.tokenTextSplitter = tokenTextSplitter;
    }

    @GetMapping("/etl")
    public String etl() {
        //读取文档
        List<Document> docsFromPdfWithCatalog = reader.getDocsFromPdf();
        //转换
        List<Document> splitDocuments = tokenTextSplitter.splitDocuments(docsFromPdfWithCatalog);
        for (Document doc : splitDocuments) {
            System.out.println("Chunk: " + doc.getFormattedContent());
            System.out.println("Metadata: " + doc.getMetadata());
        }

        //TODO https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html
        //TODO 写入向量库
        return "success";
    }

}
