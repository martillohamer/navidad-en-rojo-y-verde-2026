package reto.extension.radar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SleighTelemetryServer {

    // Un latido de telemetria del trineo en vuelo.
    public record Telemetria(double latitud, double longitud, double velocidadKmh, String estadoBateria) {
    }

    private final ConcurrentLinkedQueue<Telemetria> historial = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean activo = new AtomicBoolean(false);
    private ScheduledExecutorService planificador;
    private ScheduledFuture<?> tarea;
    private double latitud = 0.0;
    private double longitud = 0.0;
    private double velocidadKmh = 0.0;

    // Inicia la emision periodica de telemetria cada periodoMilisegundos.
    public synchronized void iniciar(long periodoMilisegundos) {
        if (periodoMilisegundos <= 0) {
            throw new IllegalArgumentException("El periodo debe ser positivo: " + periodoMilisegundos);
        }
        if (activo.get()) {
            return;
        }
        planificador = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread hilo = new Thread(r, "sleigh-telemetry");
            hilo.setDaemon(true);
            return hilo;
        });
        activo.set(true);
        tarea = planificador.scheduleAtFixedRate(this::emitirLatido, 0, periodoMilisegundos, TimeUnit.MILLISECONDS);
    }

    // Emite un unico latido de telemetria, avanzando la posicion del trineo.
    public synchronized Telemetria emitirLatido() {
        latitud += 0.5;
        longitud += 0.3;
        velocidadKmh = Math.min(velocidadKmh + 10.0, 300.0);
        Telemetria telemetria = new Telemetria(latitud, longitud, velocidadKmh, estadoBateria(velocidadKmh));
        historial.add(telemetria);
        return telemetria;
    }

    // Parada limpia: detiene la emision y libera el planificador.
    public synchronized void detener() {
        if (tarea != null) {
            tarea.cancel(false);
            tarea = null;
        }
        if (planificador != null) {
            planificador.shutdownNow();
            planificador = null;
        }
        activo.set(false);
    }

    // Espera a que el planificador termine, con un limite de tiempo.
    public boolean esperarTerminacion(long timeoutMilisegundos) throws InterruptedException {
        ScheduledExecutorService actual = planificador;
        if (actual == null) {
            return true;
        }
        return actual.awaitTermination(timeoutMilisegundos, TimeUnit.MILLISECONDS);
    }

    public boolean activo() {
        return activo.get();
    }

    public List<Telemetria> historial() {
        return new ArrayList<>(historial);
    }

    public int totalLatidos() {
        return historial.size();
    }

    private static String estadoBateria(double velocidad) {
        if (velocidad >= 250.0) {
            return "BAJA";
        }
        if (velocidad >= 120.0) {
            return "MEDIA";
        }
        return "ALTA";
    }
}
