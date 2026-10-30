import { NextRequest, NextResponse } from "next/server";


export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const question = body?.question;

    if (typeof question !== "string" || !question.trim()) {
      return NextResponse.json(
        { error: "Debes proporcionar una pregunta." },
        { status: 400 }
      );
    }

    const backendUrl =
      process.env.RAG_BACKEND_URL ?? "http://127.0.0.1:8082";

    const response = await fetch(`${backendUrl}/api/v1/chat`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ question: question.trim() }),
      signal: AbortSignal.timeout(60000),
      cache: "no-store",
    });

    const responseBody = await response.text();

    return new Response(responseBody, {
      status: response.status,
      headers: {
        "Content-Type":
          response.headers.get("Content-Type") ?? "application/json",
        "Cache-Control": "no-store",
      },
    });
  } catch (error) {
    console.error("Error en el BFF de AQUORA:", error);

    return NextResponse.json(
      {
        error:
        "No fue posible comunicarse con el servicio de Chatbot RAG. Verifica que Spring Boot esté funcionando.",
      },
      { status: 502 }
    );
  }
}
