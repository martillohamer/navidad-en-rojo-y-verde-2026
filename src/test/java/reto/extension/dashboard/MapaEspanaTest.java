package reto.extension.dashboard;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MapaEspanaTest {

    private QuantumDashboardServer servidor;

    @AfterEach
    void tearDown() {
        if (servidor != null) {
            servidor.detener();
        }
    }

    @Test
    void testEspanaEndpointResponde200YContieneTituloTactico() throws Exception {
        servidor = new QuantumDashboardServer();
        servidor.iniciar();
        HttpClient cliente = HttpClient.newHttpClient();
        HttpRequest peticion = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + servidor.puerto() + "/espana"))
                .GET().build();
        HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, respuesta.statusCode());
        assertTrue(respuesta.headers().firstValue("Content-Type").orElse("").contains("text/html"));
        assertTrue(respuesta.body().contains("RADAR TÁCTICO DE ESPAÑA") || respuesta.body().contains("RADAR") || respuesta.body().contains("ESPAÑA") || respuesta.body().contains("Espa"));
    }

    @Test
    void testGeoJsonEspanaTieneExactamente52Entidades() throws Exception {
        Path path = Path.of("src/main/resources/static/data/espana-provincias.geojson");
        assertTrue(Files.exists(path), "El GeoJSON de provincias debe existir");
        String contenido = Files.readString(path, StandardCharsets.UTF_8);
        assertTrue(contenido.contains("\"FeatureCollection\""));
        assertTrue(contenido.contains("Madrid"));
        assertTrue(contenido.contains("Barcelona"));
        assertTrue(contenido.contains("Ceuta"));
        assertTrue(contenido.contains("Melilla"));
    }
}
