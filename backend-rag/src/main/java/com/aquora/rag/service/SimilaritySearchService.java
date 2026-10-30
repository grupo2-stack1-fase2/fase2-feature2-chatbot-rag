package com.aquora.rag.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class SimilaritySearchService {

    private final DocumentEmbeddingService documentEmbeddingService;
    private final EmbeddingService embeddingService;
    private final VectorStoreService vectorStoreService;

    public SimilaritySearchService(
            DocumentEmbeddingService documentEmbeddingService,
            EmbeddingService embeddingService,
            VectorStoreService vectorStoreService) {
        this.documentEmbeddingService = documentEmbeddingService;
        this.embeddingService = embeddingService;
        this.vectorStoreService = vectorStoreService;
    }

    public List<SearchResult> search(String question, int topK)
            throws IOException {

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException(
                    "La pregunta no puede estar vacía.");
        }

        if (topK < 1) {
            throw new IllegalArgumentException(
                    "El número de resultados debe ser mayor que cero.");
        }

        // Garantiza que el manual esté indexado en PostgreSQL.
        String documentName =
                documentEmbeddingService.ensureDocumentIndexed();

        // Solo genera el embedding de la pregunta.
        List<Double> questionEmbedding =
                embeddingService.generateEmbedding(question);

        // Busca los fragmentos más similares usando pgvector.
        List<VectorStoreService.VectorSearchResult> results =
                vectorStoreService.search(
                        documentName,
                        questionEmbedding,
                        topK
                );

        return results.stream()
                .map(result -> new SearchResult(
                        result.chunkIndex(),
                        result.text(),
                        result.score()
                ))
                .toList();
    }

    public record SearchResult(
            int chunkIndex,
            String text,
            double score) {
    }
}
