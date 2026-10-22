package reto.extension.dashboard;

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

class QuantumDashboardServerTest {

    private QuantumDashboardServer servidor;

    @AfterEach
    void apagar() {
        if (servidor != null) {
            servidor.detener();
        }
    }

    private HttpResponse<String> pedir(int puerto, String ruta) throws Exception {
        HttpClient cliente = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
        HttpRequest peticion = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + puerto + ruta))
                .timeout(Duration.ofSeconds(3)).GET().build();
        return cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void testArranqueAsignaUnPuertoValido() throws IOException {
        servidor = new QuantumDashboardServer();
        servidor.iniciar();
        assertTrue(servidor.activo());
        assertTrue(servidor.puerto() > 0);
        assertEquals(0, servidor.puertoSolicitado());
    }

    @Test
    void testPuertoInvalidoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new QuantumDashboardServer(-1));
        assertThrows(IllegalArgumentException.class, () -> new QuantumDashboardServer(70000));
    }

    @Test
    void testIndexDevuelveHtml() throws Exception {
        servidor = new QuantumDashboardServer();
        servidor.iniciar();
        HttpResponse<String> r = pedir(servidor.puerto(), QuantumDashboardServer.RUTA_INDEX);
        assertEquals(200, r.statusCode());
        assertTrue(r.headers().firstValue("Content-Type").orElse("").startsWith("text/html"));
        assertTrue(r.body().contains("Quantum Dashboard"));
    }

    @Test
    void testHealthDevuelveOk() throws Exception {
        servidor = new QuantumDashboardServer();
        servidor.iniciar();
        HttpResponse<String> r = pedir(servidor.puerto(), QuantumDashboardServer.RUTA_HEALTH);
        assertEquals(200, r.statusCode());
        assertTrue(r.body().contains("\"estado\":\"OK\""));
    }

    @Test
    void testTelemetriaDevuelveJsonConCampos() throws Exception {
        servidor = new QuantumDashboardServer();
        servidor.iniciar();
        HttpResponse<String> r = pedir(servidor.puerto(), QuantumDashboardServer.RUTA_TELEMETRIA);
        assertEquals(200, r.statusCode());
        String cuerpo = r.body();
        assertTrue(cuerpo.contains("\"nodos\":24"));
        assertTrue(cuerpo.contains("\"latenciaMs\":"));
        assertTrue(cuerpo.contains("\"entropia\":"));
    }

    @Test
    void testApagadoDejaElServidorInactivo() throws IOException {
        servidor = new QuantumDashboardServer();
        servidor.iniciar();
        servidor.detener();
        assertFalse(servidor.activo());
    }

    @Test
    void testIniciarDosVecesEsIdempotente() throws IOException {
        servidor = new QuantumDashboardServer();
        servidor.iniciar();
        int puerto = servidor.puerto();
        servidor.iniciar();
        assertEquals(puerto, servidor.puerto());
        assertTrue(servidor.activo());
    }

    @Test
    void testDetenerSinIniciarNoFalla() throws IOException {
        servidor = new QuantumDashboardServer();
        servidor.detener();
        assertFalse(servidor.activo());
    }

    @Test
    void testPuertoFijoSolicitado() throws IOException {
        servidor = new QuantumDashboardServer(0);
        servidor.iniciar();
        assertEquals(0, servidor.puertoSolicitado());
        assertTrue(servidor.puerto() > 0);
    }
}
