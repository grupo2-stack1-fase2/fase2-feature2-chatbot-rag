package com.aquora.rag.controller;

import com.aquora.rag.service.ChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatService.ChatResponse ask(
            @RequestBody ChatRequest request) throws IOException {

        if (request == null || request.question() == null
                || request.question().isBlank()) {
            throw new IllegalArgumentException(
                    "La pregunta no puede estar vacía.");
        }

        return chatService.ask(request.question());
    }

    public record ChatRequest(String question) {
    }
}