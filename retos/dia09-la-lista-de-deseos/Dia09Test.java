import java.util.List;
package reto.dia09;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class Dia09Test {

    @Test
    void testContarRegalosFrecuencias() {
        ListaDeseos lista = new ListaDeseos();
        List<String> deseos = Arrays.asList("tren", "muneca", "tren", "coche", "muneca", "tren");
        Map<String, Integer> conteo = lista.contarRegalos(deseos);

        assertEquals(3, conteo.get("tren"));
        assertEquals(2, conteo.get("muneca"));
        assertEquals(1, conteo.get("coche"));
    }
}