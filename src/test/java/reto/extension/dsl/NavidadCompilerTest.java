package reto.extension.dsl;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NavidadCompilerTest {

    private final NavidadCompiler compilador = new NavidadCompiler();

    @Test
    void testCompilaUnaInstruccionValidaCompleta() {
        NavidadCompiler.InstruccionRegalo i =
                compilador.compilar("REGALO \"tren de madera\" [Prioridad: ALTA] -> DESTINO: Madrid");
        assertEquals("tren de madera", i.regalo());
        assertEquals("ALTA", i.prioridad());
        assertEquals("Madrid", i.destino());
    }

    @Test
    void testCompilaConPrioridadMediaYBaja() {
        assertEquals("MEDIA", compilador.compilar(
                "REGALO \"muneca\" [Prioridad: MEDIA] -> DESTINO: Lima").prioridad());
        assertEquals("BAJA", compilador.compilar(
                "REGALO \"puzzle\" [Prioridad: BAJA] -> DESTINO: Quito").prioridad());
    }

    @Test
    void testIgnoraEspaciosExtraYRecortaDestino() {
        NavidadCompiler.InstruccionRegalo i = compilador.compilar(
                "  REGALO   \"bici\"   [Prioridad:  ALTA]  ->  DESTINO:   Buenos Aires   ");
        assertEquals("bici", i.regalo());
        assertEquals("ALTA", i.prioridad());
        assertEquals("Buenos Aires", i.destino());
    }

    @Test
    void testPrioridadInvalidaLanzaExcepcion() {
        assertThrows(NavidadCompiler.SintaxisNavidadException.class, () ->
                compilador.compilar("REGALO \"tren\" [Prioridad: URGENTE] -> DESTINO: Madrid"));
    }

    @Test
    void testSinPalabraClaveRegaloLanzaExcepcion() {
        assertThrows(NavidadCompiler.SintaxisNavidadException.class, () ->
                compilador.compilar("OBSEQUIO \"tren\" [Prioridad: ALTA] -> DESTINO: Madrid"));
    }

    @Test
    void testSinDestinoLanzaExcepcion() {
        assertThrows(NavidadCompiler.SintaxisNavidadException.class, () ->
                compilador.compilar("REGALO \"tren\" [Prioridad: ALTA] -> Madrid"));
    }

    @Test
    void testInstruccionVaciaONulaLanzaExcepcion() {
        assertThrows(NavidadCompiler.SintaxisNavidadException.class, () -> compilador.compilar(""));
        assertThrows(NavidadCompiler.SintaxisNavidadException.class, () -> compilador.compilar("   "));
        assertThrows(NavidadCompiler.SintaxisNavidadException.class, () -> compilador.compilar(null));
    }

    @Test
    void testEsValidaDetectaCorrectamente() {
        assertTrue(compilador.esValida("REGALO \"tren\" [Prioridad: ALTA] -> DESTINO: Madrid"));
        assertFalse(compilador.esValida("REGALO \"tren\" -> DESTINO: Madrid"));
        assertFalse(compilador.esValida(null));
    }

    @Test
    void testCompilaScriptMultilineaIgnorandoVacias() {
        String script = """
                REGALO "tren" [Prioridad: ALTA] -> DESTINO: Madrid

                REGALO "muñeca" [Prioridad: BAJA] -> DESTINO: Cádiz
                """;
        List<NavidadCompiler.InstruccionRegalo> instrucciones = compilador.compilarScript(script);
        assertEquals(2, instrucciones.size());
        assertEquals("tren", instrucciones.get(0).regalo());
        assertEquals("muñeca", instrucciones.get(1).regalo());
        assertEquals("Cádiz", instrucciones.get(1).destino());
    }

    @Test
    void testScriptConLineaInvalidaLanzaExcepcion() {
        String script = """
                REGALO "tren" [Prioridad: ALTA] -> DESTINO: Madrid
                linea basura
                """;
        assertThrows(NavidadCompiler.SintaxisNavidadException.class,
                () -> compilador.compilarScript(script));
    }

    @Test
    void testScriptNuloLanzaExcepcion() {
        assertThrows(NavidadCompiler.SintaxisNavidadException.class,
                () -> compilador.compilarScript(null));
    }

    @Test
    void testRecordInstruccionExponeSusComponentes() {
        NavidadCompiler.InstruccionRegalo i = new NavidadCompiler.InstruccionRegalo("x", "ALTA", "y");
        assertEquals("x", i.regalo());
        assertEquals("ALTA", i.prioridad());
        assertEquals("y", i.destino());
        assertEquals(new NavidadCompiler.InstruccionRegalo("x", "ALTA", "y"), i);
    }
}
