
package com.aquora.rag.controller;

import com.aquora.rag.service.DocumentProcessingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentProcessingService documentService;

    public DocumentController(
            DocumentProcessingService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/manual/chunks")
    public DocumentProcessingService.DocumentResult getChunks()
            throws IOException {
        return documentService.processDocument();
    }
}
