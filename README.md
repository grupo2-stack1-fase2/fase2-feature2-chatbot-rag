# Chatbot con RAG — AQUORA

Sistema de chatbot que utiliza Retrieval-Augmented Generation (RAG)
para responder preguntas a partir del manual de usuario de AQUORA.

## Arquitectura

- Backend: Spring Boot y Java 17.
- Base de datos vectorial: PostgreSQL con pgvector.
- BFF y frontend del chat: Next.js, React y TypeScript.
- Generación de embeddings: Ollama con `nomic-embed-text`.
- Generación de respuestas: Ollama con `qwen2.5:3b`.

## Componentes

- `backend-rag/`: API, procesamiento de documentos, generación de embeddings,
  almacenamiento de vectores y búsqueda por similitud.
- `bff-nextjs/`: BFF de Next.js, interfaz del chat y endpoint `/api/chat`.
- `knowledge-base/`: documentos fuente del chatbot.

## Flujo de una consulta

1. El usuario envía una pregunta desde el chat.
2. El BFF recibe la solicitud mediante `POST /api/chat`.
3. El BFF reenvía la pregunta al backend RAG mediante `POST /api/v1/chat`.
4. El backend genera el embedding de la pregunta.
5. PostgreSQL con pgvector recupera los fragmentos más similares del manual.
6. Ollama genera una respuesta utilizando el contexto recuperado.
7. El backend devuelve la respuesta y las fuentes al cliente.

## Configuración

El backend RAG utiliza las siguientes variables de entorno:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

El BFF permite configurar la URL del backend mediante `RAG_BACKEND_URL`.
Si no se define, utiliza `http://127.0.0.1:8082`.

## Objetivo

Proporcionar respuestas basadas en la información del manual de AQUORA,
utilizando recuperación de contexto y generación de respuestas.

## Validación

El backend Java y el BFF de Next.js compilaron correctamente durante
las pruebas locales. PostgreSQL con pgvector también se configuró para
almacenar y consultar los fragmentos vectorizados del manual.
