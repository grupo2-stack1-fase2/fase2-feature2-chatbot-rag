# BFF Next.js — AQUORA

## 1. Descripción

Este proyecto implementa el Backend for Frontend (BFF) del chatbot de AQUORA utilizando Next.js y TypeScript.

El BFF recibe las preguntas enviadas por la aplicación principal y las reenvía al backend RAG de Spring Boot. Después, devuelve la respuesta generada al cliente.

## 2. Tecnologías utilizadas

- Next.js 16.4.3
- React 19.3.0
- TypeScript
- Node.js y npm

## 3. Arquitectura

El flujo de comunicación del chatbot es el siguiente:

1. El usuario escribe una pregunta en la interfaz del chatbot de AQUORA.
2. CodeIgniter recibe la solicitud y se comunica con el BFF.
3. El BFF de Next.js recibe la solicitud mediante `POST /api/chat`.
4. El BFF reenvía la pregunta al backend RAG de Spring Boot.
5. Spring Boot procesa la consulta y utiliza Ollama y PostgreSQL con pgvector para recuperar información y generar la respuesta.
6. El BFF devuelve la respuesta a la aplicación principal.

## 4. Requisitos previos

Antes de ejecutar el BFF, se necesita:

- Node.js y npm instalados.
- Acceso al backend RAG de Spring Boot.
- Los servicios requeridos por el backend RAG disponibles.

## 5. Instalación

Desde la carpeta `bff-nextjs`, ejecutar:

```bash
npm install
```

## 6. Configuración

El BFF permite configurar la dirección del backend RAG mediante la variable de entorno `RAG_BACKEND_URL`.

Si no se define, utiliza por defecto:

```text
http://127.0.0.1:8082
```

El backend debe exponer el endpoint:

```text
POST /api/v1/chat
```

La solicitud enviada por el BFF contiene la pregunta en formato JSON:

```json
{
  "question": "¿Qué funciones tiene el sistema AQUORA?"
}
```

No se deben incluir contraseñas ni credenciales en el código fuente o en el repositorio.

## 7. Ejecución en desarrollo

Iniciar el servidor de desarrollo:

```bash
npm run dev
```

Por defecto, Next.js utiliza el puerto `3000`. Si está ocupado, puede seleccionar otro puerto disponible.

## 8. Endpoint del BFF

### POST `/api/chat`

Recibe una pregunta del chatbot y la reenvía al backend RAG.

Ejemplo de solicitud:

```json
{
  "question": "¿Qué funciones tiene el sistema AQUORA?"
}
```

El endpoint devuelve la respuesta proporcionada por el backend RAG.

## 9. Compilación de producción

Para verificar y compilar la aplicación:

```bash
npm run build
```

Para iniciar la versión compilada:

```bash
npm run start
```

## 10. Ubicación del endpoint

El código del endpoint se encuentra en:

```text
app/api/chat/route.ts
```

Este archivo contiene la lógica que recibe las solicitudes del chatbot y las reenvía al backend RAG.

## 11. Estado de validación

Durante las pruebas locales se verificó que:

- El BFF recibe solicitudes mediante `POST /api/chat`.
- El backend RAG procesa las preguntas.
- El chatbot responde preguntas relacionadas con el manual de AQUORA.
- La aplicación principal muestra las fuentes consultadas.

Estas validaciones corresponden al entorno local de desarrollo.