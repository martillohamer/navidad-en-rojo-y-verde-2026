package reto.extension.ia;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import reto.extension.caos.GrinchChaosEngine;

class ElfReinforcementEngineTest {

    private static final GrinchChaosEngine.FaultType DESCONEXION =
            GrinchChaosEngine.FaultType.DESCONEXION_NODO;

    private List<ElfReinforcementEngine.Accion> acciones() {
        return List.of(
                new ElfReinforcementEngine.Accion("rapida-1", 1, false),
                new ElfReinforcementEngine.Accion("robusta-3-p2p", 3, true),
                new ElfReinforcementEngine.Accion("sin-reintento", 0, false));
    }

    private ElfReinforcementEngine motor() {
        return new ElfReinforcementEngine(acciones());
    }

    private GrinchChaosEngine.ResultadoCaos<String> exitoLimpio() {
        return new GrinchChaosEngine.ResultadoCaos<>("ok", false, 1, List.of());
    }

    private GrinchChaosEngine.ResultadoCaos<String> recuperadoConLatencia() {
        return new GrinchChaosEngine.ResultadoCaos<>("ok", true, 1,
                List.of(GrinchChaosEngine.FaultType.LATENCIA));
    }

    private GrinchChaosEngine.ResultadoCaos<String> falloDeRed() {
        return new GrinchChaosEngine.ResultadoCaos<>("fallback", true, 1, List.of(DESCONEXION));
    }

    @Test
    void testCalculaRecompensaPositivaParaExitoLimpio() {
        ElfReinforcementEngine motor = motor();
        assertEquals(10.0, motor.calcularRecompensa(exitoLimpio()), 1e-9);
    }

    @Test
    void testCalculaRecompensaMenorParaExitoConRecuperacion() {
        ElfReinforcementEngine motor = motor();
        assertEquals(4.0, motor.calcularRecompensa(recuperadoConLatencia()), 1e-9);
    }

    @Test
    void testCalculaPenalizacionParaFalloDeRed() {
        ElfReinforcementEngine motor = motor();
        assertEquals(-8.0, motor.calcularRecompensa(falloDeRed()), 1e-9);
    }

    @Test
    void testValorQInicialEsCero() {
        ElfReinforcementEngine motor = motor();
        assertEquals(0.0, motor.valorQ(new ElfReinforcementEngine.Estado(0, 0),
                acciones().get(0)), 1e-9);
    }

    @Test
    void testAjustarActualizaLaTablaQConLaTasaDeAprendizaje() {
        ElfReinforcementEngine motor = motor();
        ElfReinforcementEngine.Estado estado = new ElfReinforcementEngine.Estado(0, 0);
        ElfReinforcementEngine.Accion accion = acciones().get(0);
        ElfReinforcementEngine.Episodio episodio = motor.ajustar(estado, accion, exitoLimpio());
        assertEquals(10.0 * 0.3, episodio.qActualizado(), 1e-9);
        assertEquals(episodio.qActualizado(), motor.valorQ(estado, accion), 1e-9);
        assertTrue(episodio.exito());
    }

    @Test
    void testAjustarConFalloMarcaEpisodioNoExitoso() {
        ElfReinforcementEngine motor = motor();
        ElfReinforcementEngine.Estado estado = new ElfReinforcementEngine.Estado(1, 1);
        ElfReinforcementEngine.Episodio episodio =
                motor.ajustar(estado, acciones().get(2), falloDeRed());
        assertFalse(episodio.exito());
        assertTrue(episodio.qActualizado() < 0);
    }

    @Test
    void testPoliticaRecomendadaEligeLaMejorAccionTrasEntrenar() {
        ElfReinforcementEngine motor = motor();
        ElfReinforcementEngine.Estado estado = new ElfReinforcementEngine.Estado(99, 0);
        ElfReinforcementEngine.Accion buena = acciones().get(1);
        for (int i = 0; i < 10; i++) {
            motor.ajustar(estado, buena, exitoLimpio());
            motor.ajustar(estado, acciones().get(0), falloDeRed());
            motor.ajustar(estado, acciones().get(2), falloDeRed());
        }
        assertEquals(buena, motor.politicaRecomendada(estado));
        assertEquals(3, motor.reintentosRecomendados(estado));
    }

    @Test
    void testElAgenteMejoraSuTasaDeAciertoTrasIteraciones() {
        ElfReinforcementEngine motor = new ElfReinforcementEngine(acciones());
        ElfReinforcementEngine.Accion buena = acciones().get(1);

        java.util.function.BiFunction<ElfReinforcementEngine.Estado, ElfReinforcementEngine.Accion,
                GrinchChaosEngine.ResultadoCaos<?>> entorno =
                (estado, accion) -> accion.equals(buena) ? exitoLimpio() : falloDeRed();

        List<ElfReinforcementEngine.Episodio> historial = motor.entrenar(400, new GrinchChaosEngine(0.0), entorno);

        double primeros = motor.tasaAcierto(historial.subList(0, 100));
        double ultimos = motor.tasaAcierto(historial.subList(300, 400));
        assertTrue(ultimos > primeros,
                "La tasa de acierto debe mejorar: primeros=" + primeros + " ultimos=" + ultimos);
        assertTrue(ultimos >= 0.8, "Al final debe acertar casi siempre: " + ultimos);
    }

    @Test
    void testEpsilonDecaeTrasEntrenar() {
        ElfReinforcementEngine motor = new ElfReinforcementEngine(acciones());
        double epsilonInicial = motor.epsilon();
        motor.entrenar(50, new GrinchChaosEngine(0.0),
                (estado, accion) -> exitoLimpio());
        assertTrue(motor.epsilon() < epsilonInicial);
    }

    @Test
    void testEntrenarConEpisodiosInvalidosLanzaExcepcion() {
        ElfReinforcementEngine motor = motor();
        assertThrows(IllegalArgumentException.class,
                () -> motor.entrenar(0, new GrinchChaosEngine(0.0), (e, a) -> exitoLimpio()));
        assertThrows(IllegalArgumentException.class,
                () -> motor.entrenar(-5, new GrinchChaosEngine(0.0), (e, a) -> exitoLimpio()));
    }

    @Test
    void testEntrenarConArgumentosNulosLanzaExcepcion() {
        ElfReinforcementEngine motor = motor();
        assertThrows(NullPointerException.class, () -> motor.entrenar(10, null, (e, a) -> exitoLimpio()));
        assertThrows(NullPointerException.class, () -> motor.entrenar(10, new GrinchChaosEngine(0.0), null));
    }

    @Test
    void testConstructorConListaVaciaLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new ElfReinforcementEngine(List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> new ElfReinforcementEngine(null));
    }

    @Test
    void testConstructorValidaHiperparametros() {
        assertThrows(IllegalArgumentException.class,
                () -> new ElfReinforcementEngine(acciones(), 1.5, 0.9, 0.5, 0.99,
                        ElfReinforcementEngine.Recompensas.porDefecto(), new Random(1)));
        assertThrows(IllegalArgumentException.class,
                () -> new ElfReinforcementEngine(acciones(), 0.3, -0.1, 0.5, 0.99,
                        ElfReinforcementEngine.Recompensas.porDefecto(), new Random(1)));
    }

    @Test
    void testEstadoYAccionValidanSusCampos() {
        assertThrows(IllegalArgumentException.class, () -> new ElfReinforcementEngine.Estado(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new ElfReinforcementEngine.Estado(0, -2));
        assertThrows(IllegalArgumentException.class, () -> new ElfReinforcementEngine.Accion("", 1, true));
        assertThrows(IllegalArgumentException.class, () -> new ElfReinforcementEngine.Accion("x", -1, true));
        assertThrows(IllegalArgumentException.class, () -> new ElfReinforcementEngine.Accion(null, 1, true));
    }

    @Test
    void testAccionPorIdYAccionesExpuestas() {
        ElfReinforcementEngine motor = motor();
        assertTrue(motor.accionPorId("robusta-3-p2p").isPresent());
        assertEquals(3, motor.accionPorId("robusta-3-p2p").get().reintentos());
        assertTrue(motor.accionPorId("inexistente").isEmpty());
        assertEquals(3, motor.acciones().size());
    }

    @Test
    void testCalculaPenalizacionParaCorrupcion() {
        ElfReinforcementEngine motor = motor();
        GrinchChaosEngine.ResultadoCaos<String> corrupto = new GrinchChaosEngine.ResultadoCaos<>(
                "fallback", true, 1, List.of(GrinchChaosEngine.FaultType.CORRUPCION_PAQUETE));
        assertEquals(-8.0, motor.calcularRecompensa(corrupto), 1e-9);
    }

    @Test
    void testTasaAciertoConHistorialVacioEsCero() {
        ElfReinforcementEngine motor = motor();
        assertEquals(0.0, motor.tasaAcierto(List.of()), 1e-9);
        assertEquals(0.0, motor.tasaAcierto(null), 1e-9);
    }

    @Test
    void testDeterminismoConSemillaFija() {
        long semilla = 777L;
        ElfReinforcementEngine a = new ElfReinforcementEngine(acciones(), 0.3, 0.9, 0.5, 0.99,
                ElfReinforcementEngine.Recompensas.porDefecto(), new Random(semilla));
        ElfReinforcementEngine b = new ElfReinforcementEngine(acciones(), 0.3, 0.9, 0.5, 0.99,
                ElfReinforcementEngine.Recompensas.porDefecto(), new Random(semilla));
        java.util.function.BiFunction<ElfReinforcementEngine.Estado, ElfReinforcementEngine.Accion,
                GrinchChaosEngine.ResultadoCaos<?>> entorno =
                (estado, accion) -> accion.reintentos() >= 3 ? exitoLimpio() : falloDeRed();
        List<ElfReinforcementEngine.Episodio> ha = a.entrenar(120, new GrinchChaosEngine(0.0), entorno);
        List<ElfReinforcementEngine.Episodio> hb = b.entrenar(120, new GrinchChaosEngine(0.0), entorno);
        assertEquals(a.tasaAcierto(ha), b.tasaAcierto(hb), 1e-12);
        assertEquals(a.estadosConocidos(), b.estadosConocidos());
    }
}
