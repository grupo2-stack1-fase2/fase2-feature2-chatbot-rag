package com.aquora.rag.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentEmbeddingService {

    private final DocumentProcessingService documentProcessingService;
    private final EmbeddingService embeddingService;
    private final VectorStoreService vectorStoreService;

    public DocumentEmbeddingService(
            DocumentProcessingService documentProcessingService,
            EmbeddingService embeddingService,
            VectorStoreService vectorStoreService) {
        this.documentProcessingService = documentProcessingService;
        this.embeddingService = embeddingService;
        this.vectorStoreService = vectorStoreService;
    }

    /**
     * Genera los embeddings del manual y los devuelve.
     * Se conserva para mantener la compatibilidad con DocumentController.
     */
    public List<ChunkEmbedding> generateDocumentEmbeddings()
            throws IOException {

        DocumentProcessingService.DocumentResult document =
                documentProcessingService.processDocument();

        List<ChunkEmbedding> results = new ArrayList<>();

        for (int i = 0; i < document.chunks().size(); i++) {
            String chunk = document.chunks().get(i);

            List<Double> vector =
                    embeddingService.generateEmbedding(chunk);

            results.add(new ChunkEmbedding(i, chunk, vector));
        }

        return results;
    }

    /**
     * Garantiza que los embeddings del manual estén almacenados.
     * Si el documento ya está indexado, evita generarlos nuevamente.
     */
    @Transactional
     public String ensureDocumentIndexed() throws IOException {

        DocumentProcessingService.DocumentResult document =
                documentProcessingService.processDocument();

        String documentName = document.fileName();

        if (vectorStoreService.hasDocument(documentName)) {
        return documentName;
        }

        List<ChunkEmbedding> embeddings = generateDocumentEmbeddings();

        for (ChunkEmbedding chunk : embeddings) {
            vectorStoreService.saveChunk(
                    documentName,
                    chunk.chunkIndex(),
                    chunk.text(),
                    chunk.embedding()
            );
        }

        return documentName;
    }

    public record ChunkEmbedding(
            int chunkIndex,
            String text,
            List<Double> embedding) {
    }
}
