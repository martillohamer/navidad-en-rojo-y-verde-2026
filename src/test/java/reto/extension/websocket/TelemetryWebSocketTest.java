package reto.extension.websocket;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TelemetryWebSocketTest {

    private TelemetryWebSocketServer servidor;

    @AfterEach
    void detener() {
        if (servidor != null) {
            servidor.detener();
        }
    }

    @Test
    void testRecordNormalizaYValida() {
        TelemetryMessage m = new TelemetryMessage("  agente-1  ", " cpu ", 42.5,
                Instant.parse("2026-12-24T00:00:00Z"));
        assertEquals("agente-1", m.id());
        assertEquals("cpu", m.metricName());
        assertEquals(42.5, m.value());
    }

    @Test
    void testRecordRechazaCamposInvalidos() {
        Instant t = Instant.now();
        assertThrows(NullPointerException.class, () -> new TelemetryMessage(null, "cpu", 1.0, t));
        assertThrows(IllegalArgumentException.class, () -> new TelemetryMessage(" ", "cpu", 1.0, t));
        assertThrows(NullPointerException.class, () -> new TelemetryMessage("a", null, 1.0, t));
        assertThrows(NullPointerException.class, () -> new TelemetryMessage("a", "cpu", 1.0, null));
        assertThrows(IllegalArgumentException.class,
                () -> new TelemetryMessage("a", "cpu", Double.NaN, t));
        assertThrows(IllegalArgumentException.class,
                () -> new TelemetryMessage("a", "cpu", Double.POSITIVE_INFINITY, t));
    }

    @Test
    void testSerializacionIdaYVuelta() {
        TelemetryMessage original = new TelemetryMessage("agente-7", "latencia_ms", 88.25,
                Instant.parse("2026-12-24T18:30:00Z"));
        TelemetryMessage reconstruido = TelemetryMessage.desdeJson(original.aJson());
        assertEquals(original, reconstruido);
    }

    @Test
    void testDeserializacionDeJsonIncompletoFalla() {
        assertThrows(IllegalArgumentException.class,
                () -> TelemetryMessage.desdeJson("{\"id\":\"a\"}"));
    }

    @Test
    void testServicioGeneraFlujoDeterministaConSemilla() {
        TelemetryService a = new TelemetryService("agente", new Random(42L),
                Clock.fixed(Instant.parse("2026-12-24T00:00:00Z"), ZoneOffset.UTC));
        TelemetryService b = new TelemetryService("agente", new Random(42L),
                Clock.fixed(Instant.parse("2026-12-24T00:00:00Z"), ZoneOffset.UTC));
        assertEquals(a.siguiente(), b.siguiente());
        assertEquals(a.siguiente(), b.siguiente());
    }

    @Test
    void testServicioRecorreElCatalogoYCreceEnSecuencia() {
        TelemetryService servicio = TelemetryService.conSemilla(7L);
        List<String> catalogo = servicio.catalogo();
        for (int i = 0; i < catalogo.size() * 2; i++) {
            TelemetryMessage m = servicio.siguiente();
            assertEquals(catalogo.get(i % catalogo.size()), m.metricName());
        }
        assertEquals((long) catalogo.size() * 2, servicio.totalGenerados());
    }

    @Test
    void testServicioRechazaLoteInvalido() {
        TelemetryService servicio = TelemetryService.conSemilla(1L);
        assertThrows(IllegalArgumentException.class, () -> servicio.lote(0));
        assertEquals(3, servicio.lote(3).size());
    }

    @Test
    void testAcceptSeCalculaSegunRfc6455() throws Exception {
        servidor = new TelemetryWebSocketServer(0, TelemetryService.conSemilla(1L));
        assertEquals("s3pPLMBiTxaQ9kYGzzhZRbK+xOo=", servidor.calcularAccept("dGhlIHNhbXBsZSBub25jZQ=="));
    }

    @Test
    void testCodificacionDeTextFrameCortaYMascara() {
        byte[] marco = TelemetryWebSocketServer.codificarTextFrame("hola");
        assertEquals((byte) 0x81, marco[0]);
        assertEquals(4, marco[1]);
        assertEquals(6, marco.length);
    }

    @Test
    void testHandshakeWebSocketRealYRecepcionDeTelemetria() throws Exception {
        servidor = new TelemetryWebSocketServer(0, TelemetryService.conSemilla(2026L));
        servidor.iniciar(40);
        CompletableFuture<String> recibido = new CompletableFuture<>();
        HttpClient cliente = HttpClient.newHttpClient();
        WebSocket ws = cliente.newWebSocketBuilder()
                .buildAsync(URI.create("ws://127.0.0.1:" + servidor.puerto() + TelemetryWebSocketServer.RUTA_WS),
                        new WebSocket.Listener() {
                            @Override
                            public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                                recibido.complete(data.toString());
                                webSocket.request(1);
                                return null;
                            }
                        })
                .get(3, TimeUnit.SECONDS);
        String json = recibido.get(3, TimeUnit.SECONDS);
        assertTrue(json.contains("\"metricName\""));
        assertTrue(json.contains("\"value\""));
        TelemetryMessage mensaje = TelemetryMessage.desdeJson(json);
        assertTrue(mensaje.value() >= -40.0);
        ws.sendClose(WebSocket.NORMAL_CLOSURE, "fin").get(3, TimeUnit.SECONDS);
    }

    @Test
    void testHandshakeSinClaveDevuelveError() throws Exception {
        servidor = new TelemetryWebSocketServer(0, TelemetryService.conSemilla(1L));
        servidor.iniciar(50);
        HttpClient cliente = HttpClient.newHttpClient();
        java.net.http.HttpResponse<String> respuesta = cliente.send(
                java.net.http.HttpRequest.newBuilder()
                        .uri(URI.create("http://127.0.0.1:" + servidor.puerto()
                                + TelemetryWebSocketServer.RUTA_WS))
                        .GET().build(),
                java.net.http.HttpResponse.BodyHandlers.ofString());
        assertEquals(400, respuesta.statusCode());
    }

    @Test
    void testIniciarConPeriodoInvalidoFallaYDetenerEsLimpio() throws Exception {
        servidor = new TelemetryWebSocketServer(0, TelemetryService.conSemilla(1L));
        assertThrows(IllegalArgumentException.class, () -> servidor.iniciar(0));
        servidor.iniciar(50);
        assertTrue(servidor.activo());
        servidor.detener();
        assertFalse(servidor.activo());
        assertEquals(0, servidor.totalSuscriptores());
    }

    @Test
    void testDifundirSinSuscriptoresAvanzaLaSecuencia() throws Exception {
        servidor = new TelemetryWebSocketServer(0, TelemetryService.conSemilla(1L));
        servidor.difundir();
        servidor.difundir();
        assertEquals(2, servidor.totalEmitidos());
        assertNotEquals(0, servidor.puerto());
    }
}
