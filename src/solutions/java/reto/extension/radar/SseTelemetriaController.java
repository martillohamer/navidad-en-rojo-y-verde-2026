package reto.extension.radar;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class SseTelemetriaController {

    // Una muestra de telemetria 3D del trineo en vuelo.
    public record Telemetria3D(long secuencia, double latitud, double longitud,
            double altitud, double velocidadKmh) {

        // Formatea la telemetria como un evento SSE.
        public String comoEventoSse() {
            return "data: {\"secuencia\":" + secuencia
                    + ",\"latitud\":" + latitud
                    + ",\"longitud\":" + longitud
                    + ",\"altitud\":" + altitud
                    + ",\"velocidadKmh\":" + velocidadKmh + "}\n\n";
        }
    }

    public static final String RUTA_STREAM = "/api/vuelo/stream";
    public static final String RUTA_INDEX = "/";

    private final HttpServer servidor;
    private final ScheduledExecutorService planificador;
    private final List<HttpExchange> suscriptores = new CopyOnWriteArrayList<>();
    private final AtomicBoolean activo = new AtomicBoolean(false);
    private final AtomicLong secuencia = new AtomicLong(0);
    private ScheduledFuture<?> emision;

    public SseTelemetriaController() throws IOException {
        this(0);
    }

    // Crea el controlador. Puerto 0 = puerto efimero asignado por el sistema.
    public SseTelemetriaController(int puerto) throws IOException {
        this.servidor = HttpServer.create(new InetSocketAddress(puerto), 0);
        this.planificador = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread hilo = new Thread(r, "sse-telemetria");
            hilo.setDaemon(true);
            return hilo;
        });
        this.servidor.createContext(RUTA_STREAM, this::gestionarStream);
        this.servidor.createContext(RUTA_INDEX, this::gestionarIndex);
    }

    // Inicia el servidor HTTP y la emision periodica de telemetria SSE.
    public synchronized void iniciar(long periodoMilisegundos) {
        if (periodoMilisegundos <= 0) {
            throw new IllegalArgumentException("El periodo debe ser positivo: " + periodoMilisegundos);
        }
        if (activo.get()) {
            return;
        }
        activo.set(true);
        servidor.start();
        emision = planificador.scheduleAtFixedRate(this::difundir,
                periodoMilisegundos, periodoMilisegundos, TimeUnit.MILLISECONDS);
    }

    // Parada limpia: cierra suscriptores, detiene la emision y apaga el servidor.
    public synchronized void detener() {
        if (emision != null) {
            emision.cancel(false);
            emision = null;
        }
        for (HttpExchange suscriptor : suscriptores) {
            try {
                suscriptor.close();
            } catch (RuntimeException ignorado) {
                // cerramos "best effort"
            }
        }
        suscriptores.clear();
        servidor.stop(0);
        planificador.shutdownNow();
        activo.set(false);
    }

    // Genera una nueva muestra de telemetria avanzando la posicion del trineo.
    public Telemetria3D siguienteMuestra() {
        long s = secuencia.incrementAndGet();
        double latitud = Math.sin(s * 0.1) * 60.0;
        double longitud = (s * 3.0) % 360.0 - 180.0;
        double altitud = 1000.0 + Math.cos(s * 0.2) * 200.0;
        double velocidad = 80.0 + (s % 20) * 10.0;
        return new Telemetria3D(s, latitud, longitud, altitud, velocidad);
    }

    public int puerto() {
        return servidor.getAddress().getPort();
    }

    public boolean activo() {
        return activo.get();
    }

    public int totalSuscriptores() {
        return suscriptores.size();
    }

    public long ultimaSecuencia() {
        return secuencia.get();
    }

    // Envia una muestra de telemetria a todos los suscriptores SSE conectados.
    public void difundir() {
        if (suscriptores.isEmpty()) {
            siguienteMuestra();
            return;
        }
        String evento = siguienteMuestra().comoEventoSse();
        byte[] bytes = evento.getBytes(StandardCharsets.UTF_8);
        for (HttpExchange suscriptor : suscriptores) {
            try (OutputStream os = suscriptor.getResponseBody()) {
                os.write(bytes);
                os.flush();
            } catch (IOException e) {
                suscriptores.remove(suscriptor);
            }
        }
    }

    private void gestionarStream(HttpExchange intercambio) throws IOException {
        intercambio.getResponseHeaders().add("Content-Type", "text/event-stream");
        intercambio.getResponseHeaders().add("Cache-Control", "no-cache");
        intercambio.getResponseHeaders().add("Connection", "keep-alive");
        intercambio.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        intercambio.sendResponseHeaders(200, 0);
        suscriptores.add(intercambio);
    }

    private void gestionarIndex(HttpExchange intercambio) throws IOException {
        String html = "<!DOCTYPE html><html lang=\"es\"><head><meta charset=\"UTF-8\">"
                + "<title>Radar 3D del Polo Norte</title></head>"
                + "<body><h1>Radar 3D del Polo Norte</h1>"
                + "<p>Abre <code>src/main/resources/static/index.html</code> para el cliente Three.js.</p>"
                + "</body></html>";
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        intercambio.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        intercambio.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = intercambio.getResponseBody()) {
            os.write(bytes);
        }
    }
}
