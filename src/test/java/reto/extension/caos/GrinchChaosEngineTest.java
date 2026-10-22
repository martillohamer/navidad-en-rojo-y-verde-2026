package reto.extension.caos;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GrinchChaosEngineTest {

    private GrinchChaosEngine limpio() {
        return new GrinchChaosEngine(GrinchChaosEngine.PoliticaCaos.limpia());
    }

    private GrinchChaosEngine caosTotal() {
        return new GrinchChaosEngine(GrinchChaosEngine.PoliticaCaos.total(10L));
    }

    @Test
    void testPoliticaLimpiaNoInyectaFallosNiLatencia() {
        GrinchChaosEngine.ResultadoCaos<String> r =
                limpio().ejecutarConDiagnostico(() -> "consenso", "fallback");
        assertEquals("consenso", r.valor());
        assertFalse(r.recuperado());
        assertEquals(1, r.intentos());
        assertTrue(r.fallosInyectados().isEmpty());
    }

    @Test
    void testCaosTotalRegistraLosTresTiposDeFallo() {
        GrinchChaosEngine.ResultadoCaos<String> r =
                caosTotal().ejecutarConDiagnostico(() -> "consenso", "fallback");
        assertEquals("fallback", r.valor());
        assertTrue(r.recuperado());
        List<GrinchChaosEngine.FaultType> fallos = r.fallosInyectados();
        assertTrue(fallos.contains(GrinchChaosEngine.FaultType.LATENCIA));
        assertTrue(fallos.contains(GrinchChaosEngine.FaultType.DESCONEXION_NODO));
        assertTrue(fallos.contains(GrinchChaosEngine.FaultType.CORRUPCION_PAQUETE));
    }

    @Test
    void testDesconexionDeNodoProvocaRecuperacionConFallback() {
        GrinchChaosEngine.PoliticaCaos politica = new GrinchChaosEngine.PoliticaCaos(0.0, 1.0, 0.0, 0L);
        GrinchChaosEngine.ResultadoCaos<Integer> r =
                new GrinchChaosEngine(politica).ejecutarConDiagnostico(() -> 7, 42);
        assertEquals(42, r.valor());
        assertTrue(r.recuperado());
        assertEquals(List.of(GrinchChaosEngine.FaultType.DESCONEXION_NODO), r.fallosInyectados());
    }

    @Test
    void testCorrupcionDePaqueteProvocaRecuperacionConFallback() {
        GrinchChaosEngine.PoliticaCaos politica = new GrinchChaosEngine.PoliticaCaos(0.0, 0.0, 1.0, 0L);
        GrinchChaosEngine.ResultadoCaos<Integer> r =
                new GrinchChaosEngine(politica).ejecutarConDiagnostico(() -> 7, 42);
        assertEquals(42, r.valor());
        assertTrue(r.recuperado());
        assertEquals(List.of(GrinchChaosEngine.FaultType.CORRUPCION_PAQUETE), r.fallosInyectados());
    }

    @Test
    void testLatenciaSimuladaMarcaRecuperadoPeroNoAlteraElValor() {
        GrinchChaosEngine.PoliticaCaos politica = new GrinchChaosEngine.PoliticaCaos(1.0, 0.0, 0.0, 10L);
        GrinchChaosEngine.ResultadoCaos<String> r =
                new GrinchChaosEngine(politica).ejecutarConDiagnostico(() -> "ok", "fallback");
        assertEquals("ok", r.valor());
        assertTrue(r.recuperado());
        assertEquals(List.of(GrinchChaosEngine.FaultType.LATENCIA), r.fallosInyectados());
    }

    @Test
    void testOperacionNulaDevuelveFallbackYSeMarcaRecuperado() {
        GrinchChaosEngine.ResultadoCaos<String> r =
                limpio().ejecutarConDiagnostico(null, "fallback");
        assertEquals("fallback", r.valor());
        assertTrue(r.recuperado());
    }

    @Test
    void testOperacionQueLanzaExcepcionSeAutoRecupera() {
        GrinchChaosEngine.ResultadoCaos<Integer> r = limpio().ejecutarConDiagnostico(
                () -> {
                    throw new IllegalStateException("nodo caido");
                },
                99);
        assertEquals(99, r.valor());
        assertTrue(r.recuperado());
    }

    @Test
    void testResultadoCaosEsInmutableEnSuListaDeFallos() {
        GrinchChaosEngine.ResultadoCaos<String> r =
                caosTotal().ejecutarConDiagnostico(() -> "x", "y");
        assertThrows(UnsupportedOperationException.class,
                () -> r.fallosInyectados().add(GrinchChaosEngine.FaultType.LATENCIA));
    }

    @Test
    void testConsensoBFTSeAlcanzaSinCaos() {
        GrinchChaosEngine.ResultadoConsenso<String> r =
                limpio().alcanzarConsenso(5, 3, () -> "trineo", "abortado");
        assertTrue(r.consenso());
        assertEquals("trineo", r.valor());
        assertEquals(5, r.nodosDisponibles());
        assertEquals(3, r.nodosRequeridos());
    }

    @Test
    void testConsensoBFTFallaConCaosTotal() {
        GrinchChaosEngine.ResultadoConsenso<String> r =
                caosTotal().alcanzarConsenso(5, 3, () -> "trineo", "abortado");
        assertFalse(r.consenso());
        assertEquals("abortado", r.valor());
        assertEquals(0, r.nodosDisponibles());
    }

    @Test
    void testConsensoConNodosInvalidosLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> limpio().alcanzarConsenso(0, 1, () -> "x", "y"));
        assertThrows(IllegalArgumentException.class,
                () -> limpio().alcanzarConsenso(3, 0, () -> "x", "y"));
        assertThrows(IllegalArgumentException.class,
                () -> limpio().alcanzarConsenso(3, 4, () -> "x", "y"));
    }

    @Test
    void testDeterminismoConSemillaFija() {
        long semilla = 12345L;
        GrinchChaosEngine.PoliticaCaos politica = new GrinchChaosEngine.PoliticaCaos(0.5, 0.5, 0.5, 20L);
        GrinchChaosEngine a = new GrinchChaosEngine(0.0, politica, new Random(semilla));
        GrinchChaosEngine b = new GrinchChaosEngine(0.0, politica, new Random(semilla));
        for (int i = 0; i < 50; i++) {
            assertEquals(
                    a.ejecutarConDiagnostico(() -> "v", "f").fallosInyectados(),
                    b.ejecutarConDiagnostico(() -> "v", "f").fallosInyectados());
        }
    }

    @Test
    void testPoliticaCaosValidaSusProbabilidades() {
        assertThrows(IllegalArgumentException.class,
                () -> new GrinchChaosEngine.PoliticaCaos(-0.1, 0.0, 0.0, 0L));
        assertThrows(IllegalArgumentException.class,
                () -> new GrinchChaosEngine.PoliticaCaos(0.0, 1.1, 0.0, 0L));
        assertThrows(IllegalArgumentException.class,
                () -> new GrinchChaosEngine.PoliticaCaos(0.0, 0.0, 0.0, -1L));
    }

    @Test
    void testConstructorRechazaPoliticaOFuenteNulas() {
        assertThrows(NullPointerException.class,
                () -> new GrinchChaosEngine(0.0, null, new Random(1L)));
        assertThrows(NullPointerException.class,
                () -> new GrinchChaosEngine(0.0, GrinchChaosEngine.PoliticaCaos.limpia(), null));
    }

    @Test
    void testPoliticaYProbabilidadExpuestasPorGetters() {
        GrinchChaosEngine.PoliticaCaos politica = GrinchChaosEngine.PoliticaCaos.total(5L);
        GrinchChaosEngine engine = new GrinchChaosEngine(0.25, politica, new Random(2L));
        assertEquals(0.25, engine.probabilidadFallo(), 1e-9);
        assertEquals(politica, engine.politica());
        assertEquals(5L, engine.politica().latenciaMaximaMs());
    }
}
