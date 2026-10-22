
package com.aquora.rag.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentEmbeddingService {

    private final DocumentProcessingService documentProcessingService;
    private final EmbeddingService embeddingService;

    public DocumentEmbeddingService(
            DocumentProcessingService documentProcessingService,
            EmbeddingService embeddingService) {
        this.documentProcessingService = documentProcessingService;
        this.embeddingService = embeddingService;
    }

    public List<ChunkEmbedding> generateDocumentEmbeddings()
            throws IOException {

        DocumentProcessingService.DocumentResult document =
                documentProcessingService.processDocument();

        List<ChunkEmbedding> results = new ArrayList<>();

        for (int i = 0; i < document.chunks().size(); i++) {
            String chunk = document.chunks().get(i);

            List<Double> vector =
                    embeddingService.generateEmbedding(chunk);

            results.add(new ChunkEmbedding(
                    i,
                    chunk,
                    vector
            ));
        }

        return results;
    }

    public record ChunkEmbedding(
            int chunkIndex,
            String text,
            List<Double> embedding) {
    }
}
