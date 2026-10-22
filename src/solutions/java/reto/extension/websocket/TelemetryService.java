package reto.extension.websocket;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public final class TelemetryService {

    public static final String METRICA_CPU = "cpu_utilizacion";
    public static final String METRICA_MEMORIA = "memoria_mb";
    public static final String METRICA_LATENCIA = "latencia_ms";
    public static final String METRICA_TEMP = "temperatura_taller";

    private static final List<String> CATALOGO =
            List.of(METRICA_CPU, METRICA_MEMORIA, METRICA_LATENCIA, METRICA_TEMP);

    private final Random aleatorio;
    private final Clock reloj;
    private final AtomicLong secuencia = new AtomicLong(0);
    private final String prefijoAgente;

    public TelemetryService() {
        this("agente", new Random(), Clock.systemUTC());
    }

    public TelemetryService(String prefijoAgente, Random aleatorio, Clock reloj) {
        this.prefijoAgente = exigirPrefijo(prefijoAgente);
        this.aleatorio = Objects.requireNonNull(aleatorio, "El generador aleatorio no puede ser null");
        this.reloj = Objects.requireNonNull(reloj, "El reloj no puede ser null");
    }

    public static TelemetryService conSemilla(long semilla) {
        return new TelemetryService("agente", new Random(semilla), Clock.systemUTC());
    }

    public TelemetryMessage siguiente() {
        return siguiente(prefijoAgente);
    }

    public TelemetryMessage siguiente(String agente) {
        long s = secuencia.incrementAndGet();
        String metrica = CATALOGO.get((int) ((s - 1) % CATALOGO.size()));
        double valor = valorPara(metrica);
        String id = exigirPrefijo(agente) + "-" + metrica + "-" + s;
        return new TelemetryMessage(id, metrica, valor, reloj.instant());
    }

    public List<TelemetryMessage> lote(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva: " + cantidad);
        }
        return java.util.stream.IntStream.range(0, cantidad)
                .mapToObj(i -> siguiente())
                .toList();
    }

    private double valorPara(String metrica) {
        return switch (metrica) {
            case METRICA_CPU -> redondear(aleatorio.nextDouble() * 100.0);
            case METRICA_MEMORIA -> redondear(256.0 + aleatorio.nextDouble() * 768.0);
            case METRICA_LATENCIA -> redondear(5.0 + aleatorio.nextDouble() * 95.0);
            default -> redondear(-40.0 + aleatorio.nextDouble() * 40.0);
        };
    }

    private static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private static String exigirPrefijo(String prefijo) {
        Objects.requireNonNull(prefijo, "El prefijo del agente no puede ser null");
        String recortado = prefijo.strip();
        if (recortado.isEmpty()) {
            throw new IllegalArgumentException("El prefijo del agente no puede estar vacio");
        }
        return recortado;
    }

    public long totalGenerados() {
        return secuencia.get();
    }

    public Instant ahora() {
        return reloj.instant();
    }

    public List<String> catalogo() {
        return CATALOGO;
    }
}
