package com.aquora.rag.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class EmbeddingService {

    private final RestClient restClient;

    @Value("${rag.embedding.model:nomic-embed-text}")
    private String embeddingModel;

    public EmbeddingService(
        @Value("${rag.embedding.url:http://localhost:11434}")
        String ollamaUrl) {

    this.restClient = RestClient.builder()
            .baseUrl(ollamaUrl)
            .build();
}

    public List<Double> generateEmbedding(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "El texto no puede estar vacío."
            );
        }

        Map<String, Object> request = Map.of(
                "model", embeddingModel,
                "input", text
        );

        try {
            String response = restClient.post()
                    .uri("/api/embed")
                    .body(request)
                    .retrieve()
                    .body(String.class);

            JsonNode root = new ObjectMapper().readTree(response);
            JsonNode embedding = root.path("embeddings").path(0);

            if (!embedding.isArray() || embedding.isEmpty()) {
                throw new IllegalStateException(
                        "Ollama no devolvió un embedding válido."
                );
            }

            List<Double> vector = new ArrayList<>();

            for (JsonNode value : embedding) {
                vector.add(value.asDouble());
            }

            return vector;

        } catch (Exception e) {
            throw new IllegalStateException(
                    "No se pudo generar el embedding con Ollama: "
                            + e.getMessage(),
                    e
            );
        }
    }
}