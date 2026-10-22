package reto.extension.radar;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SseTelemetriaTest {

    private SseTelemetriaController controlador;

    @AfterEach
    void detener() {
        if (controlador != null) {
            controlador.detener();
        }
    }

    @Test
    void testSiguienteMuestraAvanzaLaSecuencia() throws IOException {
        controlador = new SseTelemetriaController();
        SseTelemetriaController.Telemetria3D m1 = controlador.siguienteMuestra();
        SseTelemetriaController.Telemetria3D m2 = controlador.siguienteMuestra();
        assertEquals(1, m1.secuencia());
        assertEquals(2, m2.secuencia());
        assertTrue(m1.velocidadKmh() > 0);
    }

    @Test
    void testComoEventoSseTieneElFormatoCorrecto() {
        SseTelemetriaController.Telemetria3D m =
                new SseTelemetriaController.Telemetria3D(7, 12.5, -40.0, 1500.0, 90.0);
        String evento = m.comoEventoSse();
        assertTrue(evento.startsWith("data: {"));
        assertTrue(evento.endsWith("\n\n"));
        assertTrue(evento.contains("\"secuencia\":7"));
        assertTrue(evento.contains("\"latitud\":12.5"));
    }

    @Test
    void testIniciarActivaElServidor() throws IOException, InterruptedException {
        controlador = new SseTelemetriaController();
        controlador.iniciar(50);
        assertTrue(controlador.activo());
        assertTrue(controlador.puerto() > 0);
    }

    @Test
    void testIniciarConPeriodoInvalidoLanzaExcepcion() throws IOException {
        controlador = new SseTelemetriaController();
        assertThrows(IllegalArgumentException.class, () -> controlador.iniciar(0));
        assertThrows(IllegalArgumentException.class, () -> controlador.iniciar(-1));
    }

    @Test
    void testElEndpointSseRespondeConCabecerasCorrectas() throws Exception {
        controlador = new SseTelemetriaController();
        controlador.iniciar(30);
        HttpClient cliente = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3)).build();
        HttpRequest peticion = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + controlador.puerto() + SseTelemetriaController.RUTA_STREAM))
                .timeout(Duration.ofSeconds(3))
                .GET().build();
        HttpResponse<String> respuesta = cliente.send(peticion,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, respuesta.statusCode());
        assertEquals("text/event-stream",
                respuesta.headers().firstValue("Content-Type").orElse(""));
    }

    @Test
    void testElIndexSeSirveComoHtml() throws Exception {
        controlador = new SseTelemetriaController();
        controlador.iniciar(30);
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest peticion = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + controlador.puerto() + "/"))
                .timeout(Duration.ofSeconds(3)).GET().build();
        HttpResponse<String> respuesta = cliente.send(peticion,
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, respuesta.statusCode());
        assertTrue(respuesta.body().contains("Radar 3D"));
    }

    @Test
    void testDifundirSinSuscriptoresAvanzaLaSecuencia() throws IOException {
        controlador = new SseTelemetriaController();
        controlador.difundir();
        controlador.difundir();
        assertEquals(2, controlador.ultimaSecuencia());
    }

    @Test
    void testDetenerApagaElServidorLimpiamente() throws IOException {
        controlador = new SseTelemetriaController();
        controlador.iniciar(30);
        controlador.detener();
        assertFalse(controlador.activo());
        assertEquals(0, controlador.totalSuscriptores());
    }

    @Test
    void testDetenerSinIniciarNoFalla() throws IOException {
        controlador = new SseTelemetriaController();
        controlador.detener();
        assertFalse(controlador.activo());
    }
}
