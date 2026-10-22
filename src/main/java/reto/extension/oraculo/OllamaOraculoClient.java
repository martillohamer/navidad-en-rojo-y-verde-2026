package reto.extension.oraculo;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OllamaOraculoClient {

    public static final String URL_POR_DEFECTO = "http://localhost:11434/api/generate";
    public static final String MODELO_POR_DEFECTO = "llama3";

    private final String url;
    private final String modelo;
    private final HttpClient cliente;
    private final Duration timeout;

    public OllamaOraculoClient() {
        this(URL_POR_DEFECTO, MODELO_POR_DEFECTO);
    }

    public OllamaOraculoClient(String url, String modelo) {
        this.url = url;
        this.modelo = modelo;
        this.timeout = Duration.ofSeconds(30);
        this.cliente = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    // Construye el payload JSON que espera la API de Ollama.
    public String construirPayload(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("El prompt no puede estar vacio");
        }
        return "{"
                + "\"model\":\"" + escaparJson(modelo) + "\","
                + "\"prompt\":\"" + escaparJson(prompt) + "\","
                + "\"stream\":false"
                + "}";
    }

    // Extrae el campo "response" de la respuesta JSON de Ollama sin dependencias externas.
    public static String parsearRespuesta(String json) {
        if (json == null || json.isBlank()) {
            throw new OllamaOraculoException("Respuesta vacia del oraculo");
        }
        Matcher matcher = Pattern.compile("\"response\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"")
                .matcher(json);
        if (!matcher.find()) {
            throw new OllamaOraculoException("No se encontro el campo 'response' en: " + json);
        }
        return desescaparJson(matcher.group(1));
    }

    // Pide al oraculo una carta navideña personalizada. Puede lanzar OllamaOraculoException.
    public String generarCarta(String nombreNino, String regalo) {
        if (nombreNino == null || nombreNino.isBlank()) {
            throw new IllegalArgumentException("El nombre del nino no puede estar vacio");
        }
        String prompt = "Escribe una breve carta navidena de Papa Noel para " + nombreNino
                + " que pidio " + (regalo == null ? "un regalo sorpresa" : regalo) + ".";
        String payload = construirPayload(prompt);
        HttpRequest peticion = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(timeout)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();
        try {
            HttpResponse<String> respuesta = cliente.send(peticion,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (respuesta.statusCode() != 200) {
                throw new OllamaOraculoException(
                        "El oraculo respondio con codigo " + respuesta.statusCode());
            }
            return parsearRespuesta(respuesta.body());
        } catch (IOException e) {
            throw new OllamaOraculoException("No se pudo contactar con el oraculo: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OllamaOraculoException("Consulta al oraculo interrumpida", e);
        }
    }

    public String url() {
        return url;
    }

    public String modelo() {
        return modelo;
    }

    private static String escaparJson(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String desescaparJson(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char siguiente = s.charAt(++i);
                switch (siguiente) {
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case '/' -> sb.append('/');
                    default -> sb.append(siguiente);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // Excepcion propia del oraculo neuronal.
    public static class OllamaOraculoException extends RuntimeException {
        public OllamaOraculoException(String mensaje) {
            super(mensaje);
        }

        public OllamaOraculoException(String mensaje, Throwable causa) {
            super(mensaje, causa);
        }
    }
}
