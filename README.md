@'
# Chatbot con RAG

Sistema de chatbot que utiliza Retrieval-Augmented Generation (RAG)
para responder preguntas a partir de una base de conocimiento.

## Arquitectura

- Backend: Spring Boot
- Base de datos vectorial: PostgreSQL con pgvector
- Frontend: Next.js
- Widget: chat web embebible
- LLM: pendiente de selección

## Componentes

- `backend-rag/`: API, procesamiento de documentos,
  embeddings y recuperación de contexto.
- `bff-nextjs/`: BFF y widget de chat.
- `knowledge-base/`: documentos fuente del chatbot.

## Objetivo

Permitir que los usuarios realicen preguntas y reciban respuestas
basadas en información recuperada de los documentos de referencia.
'@ | Set-Content -Encoding utf8 README.md