package reto.extension.caos;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class GrinchChaosEngine {

    private final double probabilidadFallo;
    private final ThreadLocalRandom aleatorio;

    public GrinchChaosEngine(double probabilidadFallo) {
        if (probabilidadFallo < 0.0 || probabilidadFallo > 1.0) {
            throw new IllegalArgumentException(
                    "La probabilidad de fallo debe estar entre 0.0 y 1.0: " + probabilidadFallo);
        }
        this.probabilidadFallo = probabilidadFallo;
        this.aleatorio = ThreadLocalRandom.current();
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

    private boolean debeFallar() {
        return aleatorio.nextDouble() < probabilidadFallo;
    }

    public double probabilidadFallo() {
        return probabilidadFallo;
    }

    // Excepcion propia que representa un fallo inyectado por el Grinch.
    public static class ChaosInjectedException extends RuntimeException {
        public ChaosInjectedException(String mensaje) {
            super(mensaje);
        }
    }
}
