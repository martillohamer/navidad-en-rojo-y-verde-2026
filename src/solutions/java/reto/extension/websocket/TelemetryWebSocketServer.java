package reto.extension.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class TelemetryWebSocketServer {

    public static final String RUTA_WS = "/ws/telemetry";
    public static final String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    private final ServerSocket servidor;
    private final TelemetryService telemetria;
    private final ScheduledExecutorService planificador;
    private final List<OutputStream> suscriptores = new CopyOnWriteArrayList<>();
    private final AtomicBoolean activo = new AtomicBoolean(false);
    private Thread aceptador;
    private ScheduledFuture<?> emision;

    public TelemetryWebSocketServer() throws IOException {
        this(0, TelemetryService.conSemilla(2026L));
    }

    public TelemetryWebSocketServer(int puerto, TelemetryService telemetria) throws IOException {
        this.telemetria = Objects.requireNonNull(telemetria, "El servicio de telemetria no puede ser null");
        this.servidor = new ServerSocket();
        this.servidor.setReuseAddress(true);
        this.servidor.bind(new InetSocketAddress(puerto), 0);
        this.planificador = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread hilo = new Thread(r, "ws-telemetria-emisor");
            hilo.setDaemon(true);
            return hilo;
        });
    }

    public synchronized void iniciar(long periodoMilisegundos) {
        if (periodoMilisegundos <= 0) {
            throw new IllegalArgumentException("El periodo debe ser positivo: " + periodoMilisegundos);
        }
        if (activo.get()) {
            return;
        }
        activo.set(true);
        aceptador = new Thread(this::bucleAceptacion, "ws-telemetria-aceptador");
        aceptador.setDaemon(true);
        aceptador.start();
        emision = planificador.scheduleAtFixedRate(this::difundir,
                periodoMilisegundos, periodoMilisegundos, TimeUnit.MILLISECONDS);
    }

    public synchronized void detener() {
        if (emision != null) {
            emision.cancel(false);
            emision = null;
        }
        activo.set(false);
        suscriptores.clear();
        try {
            servidor.close();
        } catch (IOException ignorado) {
        }
        planificador.shutdownNow();
    }

    public void difundir() {
        TelemetryMessage mensaje = telemetria.siguiente();
        byte[] marco = codificarTextFrame(mensaje.aJson());
        for (OutputStream suscriptor : suscriptores) {
            try {
                suscriptor.write(marco);
                suscriptor.flush();
            } catch (IOException e) {
                suscriptores.remove(suscriptor);
            }
        }
    }

    public String calcularAccept(String claveCliente) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] digest = sha1.digest((claveCliente + GUID).getBytes(StandardCharsets.US_ASCII));
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 no disponible", e);
        }
    }

    static byte[] codificarTextFrame(String texto) {
        byte[] carga = texto.getBytes(StandardCharsets.UTF_8);
        int n = carga.length;
        int longitudCabecera = n <= 125 ? 2 : (n <= 65535 ? 4 : 10);
        byte[] marco = new byte[longitudCabecera + n];
        marco[0] = (byte) 0x81;
        if (n <= 125) {
            marco[1] = (byte) n;
        } else if (n <= 65535) {
            marco[1] = (byte) 126;
            marco[2] = (byte) ((n >> 8) & 0xFF);
            marco[3] = (byte) (n & 0xFF);
        } else {
            marco[1] = (byte) 127;
            long l = n;
            for (int i = 0; i < 8; i++) {
                marco[2 + i] = (byte) ((l >> (8 * (7 - i))) & 0xFF);
            }
        }
        System.arraycopy(carga, 0, marco, longitudCabecera, n);
        return marco;
    }

    private void bucleAceptacion() {
        while (activo.get()) {
            try {
                Socket cliente = servidor.accept();
                Thread hilo = new Thread(() -> atenderCliente(cliente), "ws-telemetria-cliente");
                hilo.setDaemon(true);
                hilo.start();
            } catch (IOException e) {
                if (!activo.get()) {
                    return;
                }
            }
        }
    }

    private void atenderCliente(Socket cliente) {
        try (cliente) {
            cliente.setTcpNoDelay(true);
            BufferedReader lector = new BufferedReader(
                    new InputStreamReader(cliente.getInputStream(), StandardCharsets.ISO_8859_1));
            String peticion = lector.readLine();
            String clave = null;
            String ruta = null;
            if (peticion != null) {
                String[] partes = peticion.split(" ");
                if (partes.length >= 2) {
                    ruta = partes[1];
                }
            }
            String linea;
            while ((linea = lector.readLine()) != null && !linea.isEmpty()) {
                int sep = linea.indexOf(':');
                if (sep > 0 && linea.substring(0, sep).trim().equalsIgnoreCase("Sec-WebSocket-Key")) {
                    clave = linea.substring(sep + 1).trim();
                }
            }
            OutputStream salida = cliente.getOutputStream();
            if (!RUTA_WS.equals(ruta)) {
                responderError(salida, "404 Not Found");
                return;
            }
            if (clave == null || clave.isBlank()) {
                responderError(salida, "400 Bad Request");
                return;
            }
            String respuesta = "HTTP/1.1 101 Switching Protocols\r\n"
                    + "Upgrade: websocket\r\n"
                    + "Connection: Upgrade\r\n"
                    + "Sec-WebSocket-Accept: " + calcularAccept(clave) + "\r\n\r\n";
            salida.write(respuesta.getBytes(StandardCharsets.ISO_8859_1));
            salida.flush();
            suscriptores.add(salida);
            drenar(cliente.getInputStream(), salida);
        } catch (IOException ignorado) {
        }
    }

    private void drenar(InputStream entrada, OutputStream salida) {
        try (entrada) {
            byte[] buffer = new byte[1024];
            while (entrada.read(buffer) != -1) {
                continue;
            }
        } catch (IOException ignorado) {
        } finally {
            suscriptores.remove(salida);
        }
    }

    private void responderError(OutputStream salida, String estado) throws IOException {
        byte[] cuerpo = ("{\"error\":\"" + estado + "\"}").getBytes(StandardCharsets.UTF_8);
        String cabeceras = "HTTP/1.1 " + estado + "\r\n"
                + "Content-Type: application/json; charset=utf-8\r\n"
                + "Content-Length: " + cuerpo.length + "\r\n"
                + "Connection: close\r\n\r\n";
        salida.write(cabeceras.getBytes(StandardCharsets.ISO_8859_1));
        salida.write(cuerpo);
        salida.flush();
    }

    public int puerto() {
        return servidor.getLocalPort();
    }

    public boolean activo() {
        return activo.get();
    }

    public int totalSuscriptores() {
        return suscriptores.size();
    }

    public long totalEmitidos() {
        return telemetria.totalGenerados();
    }
}
