
package com.aquora.rag.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentProcessingService {

    @Value("${rag.document.path:../knowledge-base/Manual_de_usuario_AQUORA.txt}")
    private String documentPath;

    private static final int CHUNK_SIZE = 1000;
    private static final int CHUNK_OVERLAP = 150;

    public DocumentResult processDocument() throws IOException {
        Path path = Path.of(documentPath);

        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new IOException(
                "No se encontró el documento: " +
                path.toAbsolutePath()
            );
        }

        String text = Files.readString(path, StandardCharsets.UTF_8);
        text = cleanText(text);

        if (text.isBlank()) {
            throw new IOException(
                "El documento no contiene texto."
            );
        }

        List<String> chunks = splitIntoChunks(text);

        return new DocumentResult(
            path.getFileName().toString(),
            text.length(),
            chunks
        );
    }

    private String cleanText(String text) {
        return text
            .replace("\uFEFF", "")
            .replace("\r\n", "\n")
            .replace('\r', '\n')
            .replaceAll("[\\t ]+", " ")
            .replaceAll(" *\\n *", "\n")
            .replaceAll("\\n{3,}", "\n\n")
            .trim();
    }





private List<String> splitIntoChunks(String text) {
    List<String> chunks = new ArrayList<>();
    int start = 0;

    while (start < text.length()) {
        int end = Math.min(start + CHUNK_SIZE, text.length());

        if (end < text.length()) {
            int lastSpace = text.lastIndexOf(' ', end - 1);
            int lastNewline = text.lastIndexOf('\n', end - 1);
            int boundary = Math.max(lastSpace, lastNewline);

            if (boundary > start + CHUNK_SIZE / 2) {
                end = boundary;
            }
        }

        String chunk = text.substring(start, end).trim();


        if (chunk.length() > CHUNK_SIZE) {
            throw new IllegalStateException(
                "Se generó un fragmento superior a 1000 caracteres."
            );
        }

        if (!chunk.isEmpty()) {
            chunks.add(chunk);
        }

        if (end >= text.length()) {
            break;
        }

        start = end - CHUNK_OVERLAP;
    }

    return chunks;
}





    public record DocumentResult(
        String fileName,
        int totalCharacters,
        List<String> chunks
    ) {}
}
