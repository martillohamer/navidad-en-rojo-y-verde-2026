package reto.extension.oraculo;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OllamaOraculoTest {

    private HttpServer servidor;
    private String urlFalsa;
    private String ultimoCuerpoRecibido;

    @BeforeEach
    void levantarServidorFalso() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/api/generate", intercambio -> {
            ultimoCuerpoRecibido = new String(
                    intercambio.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String cuerpo = "{\"model\":\"llama3\",\"response\":\"Feliz Navidad, Ana!\",\"done\":true}";
            byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
            intercambio.getResponseHeaders().add("Content-Type", "application/json");
            intercambio.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = intercambio.getResponseBody()) {
                os.write(bytes);
            }
        });
        servidor.start();
        urlFalsa = "http://127.0.0.1:" + servidor.getAddress().getPort() + "/api/generate";
    }

    @AfterEach
    void apagarServidor() {
        if (servidor != null) {
            servidor.stop(0);
        }
    }

    @Test
    void testConstruirPayloadContieneModeloYStreamFalse() {
        OllamaOraculoClient cliente = new OllamaOraculoClient(urlFalsa, "llama3");
        String payload = cliente.construirPayload("Hola Santa");
        assertTrue(payload.contains("\"model\":\"llama3\""));
        assertTrue(payload.contains("\"stream\":false"));
        assertTrue(payload.contains("\"prompt\":\"Hola Santa\""));
    }

    @Test
    void testConstruirPayloadEscapaComillasYSaltos() {
        OllamaOraculoClient cliente = new OllamaOraculoClient(urlFalsa, "llama3");
        String payload = cliente.construirPayload("dijo \"hola\"\nnueva linea");
        assertTrue(payload.contains("\\\"hola\\\""));
        assertTrue(payload.contains("\\n"));
    }

    @Test
    void testConstruirPayloadConPromptVacioLanzaExcepcion() {
        OllamaOraculoClient cliente = new OllamaOraculoClient(urlFalsa, "llama3");
        assertThrows(IllegalArgumentException.class, () -> cliente.construirPayload(""));
        assertThrows(IllegalArgumentException.class, () -> cliente.construirPayload(null));
    }

    @Test
    void testParsearRespuestaExtraeElCampoResponse() {
        String json = "{\"model\":\"llama3\",\"response\":\"Hola Ana\",\"done\":true}";
        assertEquals("Hola Ana", OllamaOraculoClient.parsearRespuesta(json));
    }

    @Test
    void testParsearRespuestaDesescapaSecuencias() {
        String json = "{\"response\":\"Linea1\\nLinea2 \\\"cita\\\"\"}";
        assertEquals("Linea1\nLinea2 \"cita\"", OllamaOraculoClient.parsearRespuesta(json));
    }

    @Test
    void testParsearRespuestaSinCampoLanzaExcepcion() {
        assertThrows(OllamaOraculoClient.OllamaOraculoException.class,
                () -> OllamaOraculoClient.parsearRespuesta("{\"done\":true}"));
        assertThrows(OllamaOraculoClient.OllamaOraculoException.class,
                () -> OllamaOraculoClient.parsearRespuesta(""));
    }

    @Test
    void testGenerarCartaContraServidorFalsoDevuelveLaRespuesta() {
        OllamaOraculoClient cliente = new OllamaOraculoClient(urlFalsa, "llama3");
        String carta = cliente.generarCarta("Ana", "un tren");
        assertEquals("Feliz Navidad, Ana!", carta);
        assertTrue(ultimoCuerpoRecibido.contains("\"model\":\"llama3\""));
        assertTrue(ultimoCuerpoRecibido.contains("Ana"));
    }

    @Test
    void testGenerarCartaConNombreVacioLanzaExcepcion() {
        OllamaOraculoClient cliente = new OllamaOraculoClient(urlFalsa, "llama3");
        assertThrows(IllegalArgumentException.class, () -> cliente.generarCarta("", "tren"));
    }

    @Test
    void testGenerarCartaSinServidorLanzaExcepcionControlada() {
        OllamaOraculoClient cliente = new OllamaOraculoClient(
                "http://127.0.0.1:1/api/generate", "llama3");
        assertThrows(OllamaOraculoClient.OllamaOraculoException.class,
                () -> cliente.generarCarta("Ana", "tren"));
    }

    @Test
    void testValoresPorDefecto() {
        OllamaOraculoClient cliente = new OllamaOraculoClient();
        assertEquals(OllamaOraculoClient.URL_POR_DEFECTO, cliente.url());
        assertEquals("llama3", cliente.modelo());
    }
}
