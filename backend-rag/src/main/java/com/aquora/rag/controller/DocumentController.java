
package com.aquora.rag.controller;

import com.aquora.rag.service.DocumentEmbeddingService;
import com.aquora.rag.service.DocumentProcessingService;
import com.aquora.rag.service.SimilaritySearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentProcessingService documentService;
    private final DocumentEmbeddingService embeddingService;
    private final SimilaritySearchService similaritySearchService;

    public DocumentController(
            DocumentProcessingService documentService,
            DocumentEmbeddingService embeddingService,
            SimilaritySearchService similaritySearchService) {
        this.documentService = documentService;
        this.embeddingService = embeddingService;
        this.similaritySearchService = similaritySearchService;
    }

    @GetMapping("/manual/chunks")
    public DocumentProcessingService.DocumentResult getChunks()
            throws IOException {
        return documentService.processDocument();
    }

    @GetMapping("/manual/embeddings")
    public List<DocumentEmbeddingService.ChunkEmbedding> getEmbeddings()
            throws IOException {
        return embeddingService.generateDocumentEmbeddings();
    }

    @GetMapping("/manual/search")
    public List<SimilaritySearchService.SearchResult> searchManual(
            @RequestParam String question,
            @RequestParam(defaultValue = "3") int topK)
            throws IOException {
        return similaritySearchService.search(question, topK);
    }
}
