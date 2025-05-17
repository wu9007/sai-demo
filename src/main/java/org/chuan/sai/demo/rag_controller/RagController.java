package org.chuan.sai.demo.rag_controller;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    private final VectorStore vectorStore;

    public RagController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @GetMapping("/etl")
    public String etl() {
        //读取文档
        // TODO 改为由用户上传
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader("classpath:/sample1.pdf",
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                                .withNumberOfTopTextLinesToDelete(0)
                                .build())
                        .withPagesPerDocument(1)
                        .build());

        List<Document> docsFromPdfWithCatalog = pdfReader.read();

        //转换
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> splitDocuments = splitter.apply(docsFromPdfWithCatalog);
        for (Document doc : splitDocuments) {
            // 打印元数据
            System.out.println("Chunk: " + doc.getFormattedContent());
            System.out.println("Metadata: " + doc.getMetadata());
            // 自定义元数据
            doc.getMetadata().put("author", "9007");
        }

        // Add the documents to PGVector
        vectorStore.add(splitDocuments);
        return "success";
    }

    @GetMapping("/query/{text}")
    public List<Document> etl(@PathVariable String text) {
        // Retrieve documents similar to a query
        FilterExpressionBuilder filterExpressionBuilder = new FilterExpressionBuilder();
        Filter.Expression expression = filterExpressionBuilder.and(
                        filterExpressionBuilder.eq("file_name", "sample1.pdf"),
                        filterExpressionBuilder.gt("page_number", 1)
                )
                .build();
        return this.vectorStore.similaritySearch(SearchRequest.builder()
                .query(text)
                .topK(5)
                .similarityThreshold(0.5)
                .filterExpression(expression)
                .build());
    }

}
