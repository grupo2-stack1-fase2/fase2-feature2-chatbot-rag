"use client";

import { FormEvent, useEffect, useRef, useState } from "react";

type Source = {
  chunkIndex?: number;
  text?: string;
  score?: number;
  documentName?: string;
};

type ChatResponse = {
  question?: string;
  answer?: string;
  sources?: Source[];
  error?: string;
};

type Message = {
  role: "user" | "assistant";
  content: string;
  sources?: Source[];
};

export default function Home() {
  const [messages, setMessages] = useState<Message[]>([
    {
      role: "assistant",
      content:
        "Â¡Hola! Soy AQUORA, tu asistente virtual de la Oficina del Agua. Puedes preguntarme sobre la informaciÃ³n contenida en el manual de usuario.",
    },
  ]);

  const [question, setQuestion] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, loading]);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const currentQuestion = question.trim();

    if (!currentQuestion || loading) {
      return;
    }

    setError("");
    setQuestion("");
    setMessages((previous) => [
      ...previous,
      { role: "user", content: currentQuestion },
    ]);
    setLoading(true);

    try {
      const response = await fetch("/api/chat", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ question: currentQuestion }),
      });

      const data: ChatResponse = await response.json();

      if (!response.ok) {
        throw new Error(
          data.error || "No fue posible obtener una respuesta del asistente."
        );
      }

      if (typeof data.answer !== "string" || !data.answer.trim()) {
        throw new Error("El asistente no devolviÃ³ una respuesta vÃ¡lida.");
      }

      setMessages((previous) => [
        ...previous,
        {
          role: "assistant",
          content: data.answer!,
          sources: Array.isArray(data.sources) ? data.sources : [],
        },
      ]);
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "OcurriÃ³ un error al comunicarse con AQUORA."
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="chat-shell">
      <header className="chat-header">
        <div className="brand-mark" aria-hidden="true">
          A
        </div>

        <div>
          <h1 className="brand-name">AQUORA</h1>
          <p className="brand-description">
            Asistente virtual de la Oficina del Agua
          </p>
        </div>
      </header>

      <main className="chat-container">
        <section className="chat-intro">
          <h2>Â¿En quÃ© puedo ayudarte?</h2>
          <p>
            Escribe tu pregunta y consultarÃ© la informaciÃ³n disponible en el
            manual de usuario de AQUORA.
          </p>
        </section>

        <section
          className="chat-messages"
          aria-label="ConversaciÃ³n con AQUORA"
          aria-live="polite"
        >
          {messages.map((message, index) => (
            <article
              key={`${message.role}-${index}`}
              className={`message ${
                message.role === "user"
                  ? "message-user"
                  : "message-assistant"
              }`}
            >
              <span className="message-role">
                {message.role === "user" ? "TÃº" : "AQUORA"}
              </span>

              <div>{message.content}</div>

              {message.role === "assistant" &&
                message.sources &&
                message.sources.length > 0 && (
                  <div className="message-sources">
                    <strong>Fragmentos consultados</strong>
                    <ul>
                      {message.sources.map((source, sourceIndex) => (
                        <li key={`${source.chunkIndex ?? "source"}-${sourceIndex}`}>
                          {source.documentName
                            ? `${source.documentName} â€” `
                            : ""}
                          {typeof source.chunkIndex === "number"
                            ? `Fragmento ${source.chunkIndex + 1}`
                            : `Fuente ${sourceIndex + 1}`}
                        </li>
                      ))}
                    </ul>
                  </div>
                )}
            </article>
          ))}

          {loading && (
            <article className="message message-assistant">
              <span className="message-role">AQUORA</span>
              Estoy consultando el manual y preparando la respuestaâ€¦
            </article>
          )}

          <div ref={messagesEndRef} />
        </section>

        {error && (
          <div className="chat-error" role="alert">
            {error}
          </div>
        )}

        <section className="chat-composer">
          <form className="chat-form" onSubmit={handleSubmit}>
            <textarea
              className="chat-input"
              value={question}
              onChange={(event) => setQuestion(event.target.value)}
              placeholder="Escribe tu pregunta sobre AQUORA..."
              aria-label="Escribe tu pregunta"
              rows={2}
              maxLength={2000}
              disabled={loading}
              onKeyDown={(event) => {
                if (
                  event.key === "Enter" &&
                  !event.shiftKey &&
                  !event.nativeEvent.isComposing
                ) {
                  event.preventDefault();
                  event.currentTarget.form?.requestSubmit();
                }
              }}
            />

            <button
              className="chat-send"
              type="submit"
              disabled={loading || !question.trim()}
            >
              {loading ? "Consultando..." : "Enviar"}
            </button>
          </form>

          <p className="chat-help">
            Presiona Enter para enviar o Shift + Enter para escribir otra lÃ­nea.
          </p>
        </section>
      </main>

      <footer className="chat-footer">
        AQUORA Â· Asistente basado en el manual de usuario
      </footer>
    </div>
  );
}
