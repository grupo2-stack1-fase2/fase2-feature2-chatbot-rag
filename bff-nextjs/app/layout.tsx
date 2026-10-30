import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "AQUORA | Asistente Virtual",
  description:
    "Asistente virtual de la Oficina del Agua, basado en el manual de usuario de AQUORA.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="es">
      <body>{children}</body>
    </html>
  );
}
