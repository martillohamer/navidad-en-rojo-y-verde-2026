package reto.extension.caos;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

public class GrinchChaosEngine {

    // Tipos de caos que el Grinch puede inyectar en la red P2P.
    public enum FaultType {
        LATENCIA,
        DESCONEXION_NODO,
        CORRUPCION_PAQUETE
    }

    // Politica inmutable de caos: probabilidades de cada fallo y latencia maxima simulada.
    public record PoliticaCaos(double probabilidadLatencia, double probabilidadDesconexion,
            double probabilidadCorrupcion, long latenciaMaximaMs) {

        public PoliticaCaos {
            validarProbabilidad("latencia", probabilidadLatencia);
            validarProbabilidad("desconexion", probabilidadDesconexion);
            validarProbabilidad("corrupcion", probabilidadCorrupcion);
            if (latenciaMaximaMs < 0) {
                throw new IllegalArgumentException("La latencia maxima no puede ser negativa: " + latenciaMaximaMs);
            }
        }

        private static void validarProbabilidad(String nombre, double valor) {
            if (valor < 0.0 || valor > 1.0) {
                throw new IllegalArgumentException(
                        "La probabilidad de " + nombre + " debe estar entre 0.0 y 1.0: " + valor);
            }
        }

        // Politica sin caos: todo el sistema opera limpio.
        public static PoliticaCaos limpia() {
            return new PoliticaCaos(0.0, 0.0, 0.0, 0L);
        }

        // Politica de caos total: siempre inyecta los tres fallos.
        public static PoliticaCaos total(long latenciaMaximaMs) {
            return new PoliticaCaos(1.0, 1.0, 1.0, latenciaMaximaMs);
        }
    }

    // Resultado inmutable de una operacion sometida a caos y auto-recuperacion.
    public record ResultadoCaos<T>(T valor, boolean recuperado, int intentos, List<FaultType> fallosInyectados) {

        public ResultadoCaos {
            fallosInyectados = List.copyOf(fallosInyectados);
        }
    }

    // Resultado inmutable de alcanzar consenso BFT.
    public record ResultadoConsenso<T>(T valor, boolean consenso, int nodosDisponibles, int nodosRequeridos) {
    }

    private final double probabilidadFallo;
    private final PoliticaCaos politica;
    private final Random aleatorio;

    public GrinchChaosEngine(double probabilidadFallo) {
        this(probabilidadFallo, PoliticaCaos.limpia(), new Random());
    }

    public GrinchChaosEngine(PoliticaCaos politica) {
        this(0.0, politica, new Random());
    }

    // Constructor completo con fuente de aleatoriedad inyectable (determinismo en tests).
    public GrinchChaosEngine(double probabilidadFallo, PoliticaCaos politica, Random aleatorio) {
        if (probabilidadFallo < 0.0 || probabilidadFallo > 1.0) {
            throw new IllegalArgumentException(
                    "La probabilidad de fallo debe estar entre 0.0 y 1.0: " + probabilidadFallo);
        }
        this.probabilidadFallo = probabilidadFallo;
        this.politica = Objects.requireNonNull(politica, "La politica no puede ser nula");
        this.aleatorio = Objects.requireNonNull(aleatorio, "La fuente aleatoria no puede ser nula");
    }

    // Ejecuta la operacion inyectando caos: si "falla" o lanza excepcion, devuelve el fallback.
    public <T> T ejecutarConResiliencia(Supplier<T> operacionSegura, T valorFallback) {
        if (operacionSegura == null) {
            return valorFallback;
        }
        try {
            if (debeFallar()) {
                throw new ChaosInjectedException(
                        "El Grinch ha saboteado la operacion (p=" + probabilidadFallo + ")");
            }
            T resultado = operacionSegura.get();
            return resultado != null ? resultado : valorFallback;
        } catch (RuntimeException e) {
            return valorFallback;
        }
    }

    // Variante sin parametro de probabilidad: usa la configurada en el constructor.
    public <T> T ejecutar(T valorFallback, Supplier<T> operacionSegura) {
        return ejecutarConResiliencia(operacionSegura, valorFallback);
    }

    // Ejecuta una operacion aplicando latencia simulada y detectando fallos de red P2P.
    public <T> ResultadoCaos<T> ejecutarConDiagnostico(Supplier<T> operacionSegura, T valorFallback) {
        List<FaultType> fallos = new ArrayList<>();
        int latencia = simularLatencia(fallos);

        if (inyectarCon(politica.probabilidadDesconexion())) {
            fallos.add(FaultType.DESCONEXION_NODO);
        }
        if (inyectarCon(politica.probabilidadCorrupcion())) {
            fallos.add(FaultType.CORRUPCION_PAQUETE);
        }

        boolean hayFalloDeRed = fallos.contains(FaultType.DESCONEXION_NODO)
                || fallos.contains(FaultType.CORRUPCION_PAQUETE);

        if (operacionSegura == null) {
            return new ResultadoCaos<>(valorFallback, true, 1, fallos);
        }

        boolean recuperado = hayFalloDeRed || latencia > 0;
        if (hayFalloDeRed) {
            return new ResultadoCaos<>(valorFallback, recuperado, 1, fallos);
        }

        try {
            T resultado = operacionSegura.get();
            T efectivo = resultado != null ? resultado : valorFallback;
            return new ResultadoCaos<>(efectivo, recuperado, 1, fallos);
        } catch (RuntimeException e) {
            return new ResultadoCaos<>(valorFallback, true, 1, fallos);
        }
    }

    // Alcanza consenso BFT: reintenta hasta reunir nodosRequeridos nodos disponibles.
    public <T> ResultadoConsenso<T> alcanzarConsenso(int nodosTotales, int nodosRequeridos,
            Supplier<T> operacionSegura, T valorFallback) {
        if (nodosTotales <= 0) {
            throw new IllegalArgumentException("El numero de nodos debe ser positivo: " + nodosTotales);
        }
        if (nodosRequeridos <= 0 || nodosRequeridos > nodosTotales) {
            throw new IllegalArgumentException(
                    "nodosRequeridos debe estar entre 1 y " + nodosTotales + ": " + nodosRequeridos);
        }

        int disponibles = 0;
        List<FaultType> fallos = new ArrayList<>();
        for (int nodo = 0; nodo < nodosTotales; nodo++) {
            simularLatencia(fallos);
            boolean desconectado = inyectarCon(politica.probabilidadDesconexion());
            boolean corrupto = inyectarCon(politica.probabilidadCorrupcion());
            if (desconectado) {
                fallos.add(FaultType.DESCONEXION_NODO);
            }
            if (corrupto) {
                fallos.add(FaultType.CORRUPCION_PAQUETE);
            }
            if (!desconectado && !corrupto) {
                disponibles++;
            }
        }

        boolean consenso = disponibles >= nodosRequeridos;
        T valor;
        if (consenso) {
            try {
                valor = operacionSegura == null ? valorFallback : operacionSegura.get();
            } catch (RuntimeException e) {
                valor = valorFallback;
            }
        } else {
            valor = valorFallback;
        }
        return new ResultadoConsenso<>(valor, consenso, disponibles, nodosRequeridos);
    }

    private int simularLatencia(List<FaultType> fallos) {
        if (inyectarCon(politica.probabilidadLatencia())) {
            fallos.add(FaultType.LATENCIA);
            long maxima = politica.latenciaMaximaMs();
            return maxima <= 0 ? 1 : (int) (aleatorio.nextLong(maxima) + 1);
        }
        return 0;
    }

    private boolean inyectarCon(double probabilidad) {
        return aleatorio.nextDouble() < probabilidad;
    }

    private boolean debeFallar() {
        return aleatorio.nextDouble() < probabilidadFallo;
    }

    public double probabilidadFallo() {
        return probabilidadFallo;
    }

    public PoliticaCaos politica() {
        return politica;
    }

    // Excepcion propia que representa un fallo inyectado por el Grinch.
    public static class ChaosInjectedException extends RuntimeException {
        public ChaosInjectedException(String mensaje) {
            super(mensaje);
        }
    }
}
