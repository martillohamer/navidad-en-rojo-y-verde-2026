package reto.dia18;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class Dia18Test {

    private final CifradorPolar cifrador = new CifradorPolar();

    @Test
    void testCifrarMinusculas() {
        assertEquals("bcd", cifrador.cifrar("abc", 1));
    }

    @Test
    void testCifrarMayusculas() {
        assertEquals("KHOOR", cifrador.cifrar("HELLO", 3));
    }

    @Test
    void testCifrarConservaMayusculasYMinusculas() {
        assertEquals("BcD", cifrador.cifrar("AbC", 1));
    }

    @Test
    void testCifrarMantieneEspaciosYSignos() {
        assertEquals("B gjl, ñ!", cifrador.cifrar("A fik, ñ!", 1));
    }

    @Test
    void testDesplazamientoCeroNoCambiaNada() {
        assertEquals("Navidad 2026!", cifrador.cifrar("Navidad 2026!", 0));
    }

    @Test
    void testDesplazamientoNegativoRetrocede() {
        assertEquals("zab", cifrador.cifrar("abc", -1));
    }

    @Test
    void testDesplazamientoMayorQueElAlfabeto() {
        // 27 equivale a girar 1: 'a' -> 'b'.
        assertEquals("bcd", cifrador.cifrar("abc", 27));
    }

    @Test
    void testDescifrarEsLaOperacionInversa() {
        String original = "Feliz Navidad, Polares!";
        assertEquals(original, cifrador.descifrar(cifrador.cifrar(original, 5), 5));
    }

    @Test
    void testTextoNuloDevuelveNulo() {
        assertNull(cifrador.cifrar(null, 3));
        assertNull(cifrador.descifrar(null, 3));
    }
}
