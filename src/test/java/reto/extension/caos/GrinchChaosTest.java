package reto.extension.caos;

import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GrinchChaosTest {

    @Test
    void testSinCaosDevuelveElResultadoReal() {
        GrinchChaosEngine engine = new GrinchChaosEngine(0.0);
        String resultado = engine.ejecutarConResiliencia(() -> "regalo", "fallback");
        assertEquals("regalo", resultado);
    }

    @Test
    void testConCaosTotalDevuelveElFallback() {
        GrinchChaosEngine engine = new GrinchChaosEngine(1.0);
        String resultado = engine.ejecutarConResiliencia(() -> "regalo", "fallback");
        assertEquals("fallback", resultado);
    }

    @Test
    void testCapturaExcepcionDeLaOperacionYDevuelveFallback() {
        GrinchChaosEngine engine = new GrinchChaosEngine(0.0);
        Integer resultado = engine.ejecutarConResiliencia(
                () -> {
                    throw new IllegalStateException("boom");
                },
                42);
        assertEquals(42, resultado);
    }

    @Test
    void testOperacionNulaDevuelveElFallback() {
        GrinchChaosEngine engine = new GrinchChaosEngine(0.5);
        assertEquals("fallback", engine.ejecutarConResiliencia(null, "fallback"));
    }

    @Test
    void testResultadoNuloDeLaOperacionSeSustituyePorElFallback() {
        GrinchChaosEngine engine = new GrinchChaosEngine(0.0);
        Supplier<String> devuelveNulo = () -> null;
        assertEquals("fallback", engine.ejecutarConResiliencia(devuelveNulo, "fallback"));
    }

    @Test
    void testProbabilidadInvalidaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new GrinchChaosEngine(-0.1));
        assertThrows(IllegalArgumentException.class, () -> new GrinchChaosEngine(1.5));
    }

    @Test
    void testGenericoFuncionaConTiposDistintos() {
        GrinchChaosEngine engine = new GrinchChaosEngine(0.0);
        int entero = engine.ejecutarConResiliencia(() -> 7, 0);
        double decimal = engine.ejecutarConResiliencia(() -> 2.5, 0.0);
        assertEquals(7, entero);
        assertEquals(2.5, decimal, 1e-9);
    }

    @Test
    void testProbabilidadExpuestaPorElGetter() {
        GrinchChaosEngine engine = new GrinchChaosEngine(0.3);
        assertEquals(0.3, engine.probabilidadFallo(), 1e-9);
    }

    @Test
    void testExcepcionDeCaosEsRuntimeExceptionConMensaje() {
        GrinchChaosEngine.ChaosInjectedException ex =
                new GrinchChaosEngine.ChaosInjectedException("sabotaje");
        assertEquals("sabotaje", ex.getMessage());
    }
}
