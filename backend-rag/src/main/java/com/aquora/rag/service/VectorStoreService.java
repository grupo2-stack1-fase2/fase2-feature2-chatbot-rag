package com.aquora.rag.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class VectorStoreService {

    private final JdbcTemplate jdbcTemplate;

    public VectorStoreService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Comprueba si el documento ya tiene fragmentos almacenados.
     */
    public boolean hasDocument(String documentName) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM document_chunks
                WHERE document_name = ?
                """,
                Integer.class,
                documentName
        );

        return count != null && count > 0;
    }

    /**
     * Guarda o actualiza un fragmento y su embedding.
     */
    public void saveChunk(
            String documentName,
            int chunkIndex,
            String content,
            List<Double> embedding) {

        if (embedding == null || embedding.size() != 768) {
            throw new IllegalArgumentException(
                    "El embedding debe contener exactamente 768 dimensiones."
            );
        }

        String vector = toVectorLiteral(embedding);

        jdbcTemplate.update(
                """
                INSERT INTO document_chunks (
                    document_name,
                    chunk_index,
                    content,
                    embedding
                )
                VALUES (?, ?, ?, CAST(? AS vector))
                ON CONFLICT (document_name, chunk_index)
                DO UPDATE SET
                    content = EXCLUDED.content,
                    embedding = EXCLUDED.embedding,
                    created_at = NOW()
                """,
                documentName,
                chunkIndex,
                content,
                vector
        );
    }

    /**
     * Elimina los fragmentos almacenados de un documento.
     */
    public void deleteDocument(String documentName) {
        jdbcTemplate.update(
                "DELETE FROM document_chunks WHERE document_name = ?",
                documentName
        );
    }

    /**
     * Recupera los fragmentos más similares a un embedding.
     */
    public List<VectorSearchResult> search(
            String documentName,
            List<Double> questionEmbedding,
            int topK) {

        if (questionEmbedding == null || questionEmbedding.size() != 768) {
            throw new IllegalArgumentException(
                    "El embedding de consulta debe contener 768 dimensiones."
            );
        }

        if (topK < 1) {
            throw new IllegalArgumentException(
                    "El número de resultados debe ser mayor que cero."
            );
        }

        String vector = toVectorLiteral(questionEmbedding);

        return jdbcTemplate.query(
                """
                SELECT
                    chunk_index,
                    content,
                    1 - (embedding <=> CAST(? AS vector)) AS score
                FROM document_chunks
                WHERE document_name = ?
                ORDER BY embedding <=> CAST(? AS vector)
                LIMIT ?
                """,
                (rs, rowNum) -> new VectorSearchResult(
                        rs.getInt("chunk_index"),
                        rs.getString("content"),
                        rs.getDouble("score")
                ),
                vector,
                documentName,
                vector,
                topK
        );
    }

    private String toVectorLiteral(List<Double> values) {
        return values.stream()
                .map(value -> {
                    if (value == null || !Double.isFinite(value)) {
                        throw new IllegalArgumentException(
                                "El vector contiene un valor no válido."
                        );
                    }

                    return String.format(Locale.ROOT, "%.12f", value);
                })
                .collect(Collectors.joining(",", "[", "]"));
    }

    public record VectorSearchResult(
            int chunkIndex,
            String text,
            double score) {
    }
}
