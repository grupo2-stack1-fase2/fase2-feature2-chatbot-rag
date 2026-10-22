
package com.aquora.rag.service;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class SimilaritySearchService {

    private final DocumentEmbeddingService documentEmbeddingService;
    private final EmbeddingService embeddingService;

    public SimilaritySearchService(
            DocumentEmbeddingService documentEmbeddingService,
            EmbeddingService embeddingService) {
        this.documentEmbeddingService = documentEmbeddingService;
        this.embeddingService = embeddingService;
    }

    public List<SearchResult> search(String question, int topK)
            throws java.io.IOException {

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException(
                    "La pregunta no puede estar vacía.");
        }

        if (topK < 1) {
            throw new IllegalArgumentException(
                    "El número de resultados debe ser mayor que cero.");
        }

        List<Double> questionEmbedding =
                embeddingService.generateEmbedding(question);

        List<DocumentEmbeddingService.ChunkEmbedding> chunks =
                documentEmbeddingService.generateDocumentEmbeddings();

        return chunks.stream()
                .map(chunk -> new SearchResult(
                        chunk.chunkIndex(),
                        chunk.text(),
                        cosineSimilarity(
                                questionEmbedding,
                                chunk.embedding())
                ))
                .sorted(Comparator.comparingDouble(
                        SearchResult::score).reversed())
                .limit(topK)
                .toList();
    }

    private double cosineSimilarity(
            List<Double> vectorA,
            List<Double> vectorB) {

        if (vectorA.size() != vectorB.size()) {
            throw new IllegalArgumentException(
                    "Los vectores deben tener la misma dimensión.");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.size(); i++) {
            double a = vectorA.get(i);
            double b = vectorB.get(i);

            dotProduct += a * b;
            normA += a * a;
            normB += b * b;
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public record SearchResult(
            int chunkIndex,
            String text,
            double score) {
    }
}
