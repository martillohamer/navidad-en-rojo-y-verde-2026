package reto.extension.dashboard;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class QuantumDashboardServer {

    public static final String RUTA_INDEX = "/";
    public static final String RUTA_HEALTH = "/health";
    public static final String RUTA_TELEMETRIA = "/api/telemetria";

    private final int puertoSolicitado;
    private final HttpServer servidor;
    private volatile boolean activo;

    public QuantumDashboardServer() throws IOException {
        this(0);
    }

    // Puerto 0 = puerto efimero asignado por el sistema operativo.
    public QuantumDashboardServer(int puerto) throws IOException {
        if (puerto < 0 || puerto > 65535) {
            throw new IllegalArgumentException("El puerto debe estar entre 0 y 65535: " + puerto);
        }
        this.puertoSolicitado = puerto;
        this.servidor = HttpServer.create(new InetSocketAddress(puerto), 0);
        this.servidor.createContext(RUTA_INDEX, this::gestionarIndex);
        this.servidor.createContext(RUTA_HEALTH, this::gestionarHealth);
        this.servidor.createContext(RUTA_TELEMETRIA, this::gestionarTelemetria);
    }

    // Arranca el servidor en un hilo daemon dedicado.
    public synchronized void iniciar() {
        if (activo) {
            return;
        }
        servidor.setExecutor(java.util.concurrent.Executors.newSingleThreadExecutor(r -> {
            Thread hilo = new Thread(r, "quantum-dashboard");
            hilo.setDaemon(true);
            return hilo;
        }));
        servidor.start();
        activo = true;
    }

    // Apagado limpio con una demora maxima de espera.
    public synchronized void detener() {
        servidor.stop(0);
        activo = false;
    }

    public boolean activo() {
        return activo;
    }

    public int puerto() {
        return servidor.getAddress().getPort();
    }

    public int puertoSolicitado() {
        return puertoSolicitado;
    }

    private void gestionarIndex(HttpExchange intercambio) throws IOException {
        responder(intercambio, 200, "text/html; charset=utf-8", htmlDashboard());
    }

    private void gestionarHealth(HttpExchange intercambio) throws IOException {
        responder(intercambio, 200, "application/json; charset=utf-8", "{\"estado\":\"OK\"}");
    }

    private void gestionarTelemetria(HttpExchange intercambio) throws IOException {
        String json = "{\"nodos\":24,\"latenciaMs\":" + latenciaSimulada()
                + ",\"entropia\":" + entropiaSimulada() + "}";
        responder(intercambio, 200, "application/json; charset=utf-8", json);
    }

    private void responder(HttpExchange intercambio, int codigo, String tipo, String cuerpo)
            throws IOException {
        byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
        intercambio.getResponseHeaders().add("Content-Type", tipo);
        intercambio.sendResponseHeaders(codigo, bytes.length);
        try (OutputStream os = intercambio.getResponseBody()) {
            os.write(bytes);
        }
    }

    private long latenciaSimulada() {
        return 10L + (System.nanoTime() % 40L);
    }

    private double entropiaSimulada() {
        return Math.round((0.5 + (System.nanoTime() % 50L) / 100.0) * 100.0) / 100.0;
    }

    private String htmlDashboard() {
        return "<!DOCTYPE html><html lang=\"es\"><head><meta charset=\"UTF-8\">"
                + "<title>Quantum Dashboard</title></head>"
                + "<body><h1>Quantum Dashboard del Polo Norte</h1>"
                + "<canvas id=\"grafica\" width=\"800\" height=\"300\"></canvas>"
                + "<script>"
                + "const ctx=document.getElementById('grafica').getContext('2d');"
                + "async function refrescar(){"
                + "const r=await fetch('/api/telemetria');const d=await r.json();"
                + "ctx.clearRect(0,0,800,300);"
                + "ctx.fillStyle='#c81f2f';ctx.font='16px sans-serif';"
                + "ctx.fillText('Nodos: '+d.nodos,20,40);"
                + "ctx.fillText('Latencia: '+d.latenciaMs+' ms',20,70);"
                + "ctx.fillText('Entropia: '+d.entropia,20,100);}"
                + "refrescar();setInterval(refrescar,1000);"
                + "</script></body></html>";
    }
}
