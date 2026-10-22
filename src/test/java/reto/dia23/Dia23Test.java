package reto.dia23;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Dia23Test {

    private final CompresorRegalos compresor = new CompresorRegalos();

    @Test
    void testNuloDevuelveNulo() {
        assertNull(compresor.comprimir(null));
        assertNull(compresor.descomprimir(null));
    }

    @Test
    void testVacioDevuelveVacio() {
        assertEquals("", compresor.comprimir(""));
        assertEquals("", compresor.descomprimir(""));
    }

    @Test
    void testSinRepeticiones() {
        // Cada caracter distinto se emite con su cuenta 1.
        assertEquals("a1b1c1", compresor.comprimir("abc"));
    }

    @Test
    void testRepeticionSimple() {
        assertEquals("a5", compresor.comprimir("aaaaa"));
    }

    @Test
    void testSecuenciaLargaConVariasRepeticiones() {
        assertEquals("a3b2c4", compresor.comprimir("aaabbcccc"));
    }

    @Test
    void testReversibilidadConLetras() {
        String original = "aaabbccccdddddddddde";
        assertEquals(original, compresor.descomprimir(compresor.comprimir(original)));
    }

    @Test
    void testCaracteresEspecialesYDigitos() {
        // Los digitos se escapan para no confundirse con las cuentas.
        String original = "111222333!!!";
        assertEquals(original, compresor.descomprimir(compresor.comprimir(original)));
    }

    @Test
    void testSimbolosYEspacios() {
        String original = "   ***---";
        assertEquals(original, compresor.descomprimir(compresor.comprimir(original)));
    }

    @Test
    void testComprimidoInvalidoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> compresor.descomprimir("a"));
        assertThrows(IllegalArgumentException.class, () -> compresor.descomprimir("\\"));
    }
}
