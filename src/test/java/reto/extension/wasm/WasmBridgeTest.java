package reto.extension.wasm;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WasmBridgeTest {

    @Test
    void testSumarPesosConAmbosLotes() {
        assertEquals(6.0, WasmBridge.sumarPesos(
                new double[] {1.0, 2.0}, new double[] {3.0}), 1e-9);
    }

    @Test
    void testSumarPesosConLotesNulos() {
        assertEquals(0.0, WasmBridge.sumarPesos(null, null), 1e-9);
        assertEquals(2.5, WasmBridge.sumarPesos(new double[] {2.5}, null), 1e-9);
    }

    @Test
    void testNodosConConsensoSoloCuentaSiAlcanzaElMinimo() {
        boolean[] votos = {true, true, false, true};
        assertEquals(3, WasmBridge.nodosConConsenso(votos, 3));
        assertEquals(0, WasmBridge.nodosConConsenso(votos, 4));
    }

    @Test
    void testNodosConConsensoConArgumentosInvalidos() {
        assertEquals(0, WasmBridge.nodosConConsenso(null, 2));
        assertEquals(0, WasmBridge.nodosConConsenso(new boolean[] {true}, 0));
    }

    @Test
    void testHayConsensoBftConDosTercios() {
        assertTrue(WasmBridge.hayConsensoBft(new boolean[] {true, true, true}));
        assertTrue(WasmBridge.hayConsensoBft(new boolean[] {true, true, false}));
        assertFalse(WasmBridge.hayConsensoBft(new boolean[] {true, false, false}));
    }

    @Test
    void testHayConsensoBftConArrayVacioONulo() {
        assertFalse(WasmBridge.hayConsensoBft(new boolean[0]));
        assertFalse(WasmBridge.hayConsensoBft(null));
    }

    @Test
    void testHashLigeroEsDeterminista() {
        assertEquals(WasmBridge.hashLigero("trineo"), WasmBridge.hashLigero("trineo"));
    }

    @Test
    void testHashLigeroDistingueTextos() {
        assertFalse(WasmBridge.hashLigero("regalo") == WasmBridge.hashLigero("carbon"));
    }

    @Test
    void testHashLigeroConNuloEsCero() {
        assertEquals(0L, WasmBridge.hashLigero(null));
    }

    @Test
    void testSerializacionDeRegalosIdaYVuelta() {
        List<String> regalos = List.of("tren", "muneca", "bici");
        String payload = WasmBridge.serializarRegalos(regalos);
        assertEquals("tren|muneca|bici", payload);
        assertEquals(regalos, WasmBridge.deserializarRegalos(payload));
    }

    @Test
    void testSerializarRegalosVacios() {
        assertEquals("", WasmBridge.serializarRegalos(List.of()));
        assertEquals("", WasmBridge.serializarRegalos(null));
        assertTrue(WasmBridge.deserializarRegalos("").isEmpty());
        assertTrue(WasmBridge.deserializarRegalos(null).isEmpty());
    }

    @Test
    void testCrearEventoUneDatosConPuntoYComa() {
        WasmBridge.WasmEvento evento = WasmBridge.crearEvento("entrega", "tren", "Madrid");
        assertEquals("entrega", evento.tipo());
        assertEquals("tren;Madrid", evento.payload());
    }

    @Test
    void testCrearEventoConDatosNulosLosNormaliza() {
        WasmBridge.WasmEvento evento = WasmBridge.crearEvento("vacio", null);
        assertEquals("", evento.payload());
    }

    @Test
    void testCrearEventoConTipoVacioLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> WasmBridge.crearEvento("", "dato"));
        assertThrows(IllegalArgumentException.class, () -> WasmBridge.crearEvento(null, "dato"));
    }

    @Test
    void testRecordWasmEventoEsInmutableYValida() {
        WasmBridge.WasmEvento evento = new WasmBridge.WasmEvento("tipo", null);
        assertEquals("", evento.payload());
        assertThrows(IllegalArgumentException.class, () -> new WasmBridge.WasmEvento("", "x"));
    }

    @Test
    void testSerializacionDeEventoIdaYVuelta() {
        WasmBridge.WasmEvento original = new WasmBridge.WasmEvento("despegue", "lat=12;lon=-4");
        String texto = WasmBridge.serializarEvento(original);
        assertEquals("despegue:lat=12;lon=-4", texto);
        assertEquals(original, WasmBridge.deserializarEvento(texto));
    }

    @Test
    void testDeserializarEventoConFormatoInvalidoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> WasmBridge.deserializarEvento("sintipo"));
        assertThrows(IllegalArgumentException.class, () -> WasmBridge.deserializarEvento(":soloPayload"));
        assertThrows(IllegalArgumentException.class, () -> WasmBridge.deserializarEvento(""));
        assertThrows(IllegalArgumentException.class, () -> WasmBridge.deserializarEvento(null));
    }

    @Test
    void testSerializarEventoNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> WasmBridge.serializarEvento(null));
    }

    @Test
    void testEsPayloadSeguroSoloAceptaAsciiImprimible() {
        assertTrue(WasmBridge.esPayloadSeguro("tren|bici-123"));
        assertFalse(WasmBridge.esPayloadSeguro("linea\nnueva"));
        assertFalse(WasmBridge.esPayloadSeguro("acentuada: muneca\u0301"));
        assertFalse(WasmBridge.esPayloadSeguro(null));
        assertTrue(WasmBridge.esPayloadSeguro(""));
    }
}
