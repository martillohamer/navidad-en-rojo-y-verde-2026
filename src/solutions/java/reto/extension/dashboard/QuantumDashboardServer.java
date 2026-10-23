package reto.extension.dashboard;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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

public class QuantumDashboardServer {

    public static final String RUTA_INDEX = "/";
    public static final String RUTA_NEXUS = "/nexus";
    public static final String RUTA_PARAGUAY = "/paraguay";
    public static final String RUTA_HEALTH = "/health";
    public static final String RUTA_TELEMETRIA = "/api/telemetria";
    public static final String RUTA_STREAM = "/api/vuelo/stream";
    public static final String RUTA_VENDOR = "/vendor";
    public static final String RUTA_DATOS = "/data";
    public static final String RECURSO_INDEX = "static/index.html";
    public static final String RECURSO_NEXUS = "static/nexus.html";
    public static final String RECURSO_PARAGUAY = "static/paraguay.html";
    public static final String RUTA_FICHERO_INDEX = "src/main/resources/static/index.html";
    public static final String RUTA_FICHERO_NEXUS = "src/main/resources/static/nexus.html";
    public static final String FICHERO_PARAGUAY = "src/main/resources/static/paraguay.html";
    public static final String RUTA_FICHERO_ESTATICOS = "src/main/resources/static";

    public record Telemetria(long secuencia, double latitud, double longitud,
            double altitud, double velocidadKmh) {

        public String comoEventoSse() {
            return "data: {\"secuencia\":" + secuencia
                    + ",\"latitud\":" + latitud
                    + ",\"longitud\":" + longitud
                    + ",\"altitud\":" + altitud
                    + ",\"velocidadKmh\":" + velocidadKmh + "}\n\n";
        }
    }

    private final int puertoSolicitado;
    private final HttpServer servidor;
    private final ScheduledExecutorService planificador;
    private final List<HttpExchange> suscriptores = new CopyOnWriteArrayList<>();
    private final AtomicLong secuencia = new AtomicLong(0);
    private final AtomicBoolean activo;
    private ScheduledFuture<?> emision;
    private final java.util.Map<String, byte[]> cacheHtml = new java.util.concurrent.ConcurrentHashMap<>();

    public QuantumDashboardServer() throws IOException {
        this(0);
    }

    public QuantumDashboardServer(int puerto) throws IOException {
        if (puerto < 0 || puerto > 65535) {
            throw new IllegalArgumentException("El puerto debe estar entre 0 y 65535: " + puerto);
        }
        this.puertoSolicitado = puerto;
        this.activo = new AtomicBoolean(false);
        this.servidor = HttpServer.create(new InetSocketAddress(puerto), 0);
        this.planificador = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread hilo = new Thread(r, "quantum-dashboard-sse");
            hilo.setDaemon(true);
            return hilo;
        });
        this.servidor.createContext(RUTA_INDEX, this::gestionarIndex);
        this.servidor.createContext(RUTA_NEXUS, this::gestionarNexus);
        this.servidor.createContext(RUTA_PARAGUAY, this::gestionarParaguay);
        this.servidor.createContext(RUTA_HEALTH, this::gestionarHealth);
        this.servidor.createContext(RUTA_TELEMETRIA, this::gestionarTelemetria);
        this.servidor.createContext(RUTA_STREAM, this::gestionarStream);
        this.servidor.createContext(RUTA_VENDOR, this::gestionarEstatico);
        this.servidor.createContext(RUTA_DATOS, this::gestionarEstatico);
    }

    public synchronized void iniciar() {
        if (activo.get()) {
            return;
        }
        servidor.setExecutor(java.util.concurrent.Executors.newCachedThreadPool(r -> {
            Thread hilo = new Thread(r, "quantum-dashboard");
            hilo.setDaemon(true);
            return hilo;
        }));
        servidor.start();
        emision = planificador.scheduleAtFixedRate(this::difundir, 500, 500, TimeUnit.MILLISECONDS);
        activo.set(true);
    }

    public synchronized void detener() {
        if (emision != null) {
            emision.cancel(false);
            emision = null;
        }
        for (HttpExchange suscriptor : suscriptores) {
            try {
                suscriptor.close();
            } catch (RuntimeException ignorado) {
            }
        }
        suscriptores.clear();
        servidor.stop(0);
        planificador.shutdownNow();
        activo.set(false);
    }

    public boolean activo() {
        return activo.get();
    }

    public int puerto() {
        return servidor.getAddress().getPort();
    }

    public int puertoSolicitado() {
        return puertoSolicitado;
    }

    public long ultimaSecuencia() {
        return secuencia.get();
    }

    public int totalSuscriptores() {
        return suscriptores.size();
    }

    public Telemetria siguienteMuestra() {
        long s = secuencia.incrementAndGet();
        double latitud = Math.sin(s * 0.1) * 60.0;
        double longitud = (s * 3.0) % 360.0 - 180.0;
        double altitud = 1000.0 + Math.cos(s * 0.2) * 200.0;
        double velocidad = 80.0 + (s % 20) * 10.0;
        return new Telemetria(s, latitud, longitud, altitud, velocidad);
    }

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

    private void gestionarIndex(HttpExchange intercambio) throws IOException {
        servirHtml(intercambio, RUTA_FICHERO_INDEX, RECURSO_INDEX);
    }

    private void gestionarNexus(HttpExchange intercambio) throws IOException {
        servirHtml(intercambio, RUTA_FICHERO_NEXUS, RECURSO_NEXUS);
    }

    private void gestionarParaguay(HttpExchange intercambio) throws IOException {
        servirHtml(intercambio, FICHERO_PARAGUAY, RECURSO_PARAGUAY);
    }

    private void servirHtml(HttpExchange intercambio, String rutaFichero, String recurso) throws IOException {
        byte[] html = leerRecurso(rutaFichero, recurso);
        intercambio.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        intercambio.sendResponseHeaders(200, html.length);
        try (OutputStream os = intercambio.getResponseBody()) {
            os.write(html);
        }
    }

    private void gestionarEstatico(HttpExchange intercambio) throws IOException {
        String ruta = intercambio.getRequestURI().getPath();
        if (ruta.contains("..")) {
            intercambio.sendResponseHeaders(404, -1);
            intercambio.close();
            return;
        }
        String recurso = "static" + ruta;
        try {
            byte[] datos = leerRecurso(RUTA_FICHERO_ESTATICOS + ruta, recurso);
            intercambio.getResponseHeaders().add("Content-Type", tipoContenido(ruta));
            intercambio.getResponseHeaders().add("Cache-Control", "public, max-age=3600");
            intercambio.sendResponseHeaders(200, datos.length);
            try (OutputStream os = intercambio.getResponseBody()) {
                os.write(datos);
            }
        } catch (IOException e) {
            intercambio.sendResponseHeaders(404, -1);
            intercambio.close();
        }
    }

    private static String tipoContenido(String ruta) {
        String limpia = ruta.toLowerCase(java.util.Locale.ROOT);
        if (limpia.endsWith(".js") || limpia.endsWith(".mjs")) {
            return "text/javascript; charset=UTF-8";
        }
        if (limpia.endsWith(".geojson") || limpia.endsWith(".json")) {
            return "application/geo+json; charset=UTF-8";
        }
        if (limpia.endsWith(".css")) {
            return "text/css; charset=UTF-8";
        }
        if (limpia.endsWith(".png")) {
            return "image/png";
        }
        if (limpia.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "application/octet-stream";
    }

    private void gestionarHealth(HttpExchange intercambio) throws IOException {
        responderJson(intercambio, "{\"estado\":\"OK\"}");
    }

    private void gestionarTelemetria(HttpExchange intercambio) throws IOException {
        responderJson(intercambio, "{\"nodos\":24,\"latencia\":10,\"entropia\":0.5}");
    }

    private void gestionarStream(HttpExchange intercambio) throws IOException {
        intercambio.getResponseHeaders().add("Content-Type", "text/event-stream; charset=UTF-8");
        intercambio.getResponseHeaders().add("Cache-Control", "no-cache");
        intercambio.getResponseHeaders().add("Connection", "keep-alive");
        intercambio.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        intercambio.sendResponseHeaders(200, 0);
        suscriptores.add(intercambio);
    }

    private void responderJson(HttpExchange intercambio, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        intercambio.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        intercambio.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = intercambio.getResponseBody()) {
            os.write(bytes);
        }
    }

    private byte[] leerRecurso(String rutaFichero, String recurso) throws IOException {
        String clave = rutaFichero + "|" + recurso;
        byte[] cache = cacheHtml.get(clave);
        if (cache != null) {
            return cache;
        }
        Path fichero = Path.of(rutaFichero);
        if (Files.exists(fichero)) {
            byte[] bytes = Files.readAllBytes(fichero);
            cacheHtml.put(clave, bytes);
            return bytes;
        }
        try (InputStream entrada = getClass().getClassLoader().getResourceAsStream(recurso)) {
            if (entrada != null) {
                byte[] bytes = entrada.readAllBytes();
                cacheHtml.put(clave, bytes);
                return bytes;
            }
        }
        throw new IOException("No se encontro el recurso " + recurso);
    }

    public static final int PUERTO_POR_DEFECTO = 8081;

    public static void main(String[] args) throws IOException {
        int puerto = PUERTO_POR_DEFECTO;
        if (args.length > 0 && !args[0].isBlank()) {
            try {
                puerto = Integer.parseInt(args[0].strip());
            } catch (NumberFormatException e) {
                System.err.println("Puerto invalido: " + args[0] + ". Uso: " + PUERTO_POR_DEFECTO);
                System.exit(2);
            }
        }
        QuantumDashboardServer servidor = new QuantumDashboardServer(puerto);
        servidor.iniciar();
        System.out.println("Quantum Dashboard escuchando en http://localhost:" + servidor.puerto());
        Runtime.getRuntime().addShutdownHook(new Thread(servidor::detener));
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
