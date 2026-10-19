package reto.dia09;

import java.util.List;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Dia09Test {
    @Test
    public void testContarRegalos() {
        ListaDeseosSolucion solucion = new ListaDeseosSolucion();
        List<String> deseos = Arrays.asList("tren", "muneca", "tren");
        var resultado = solucion.contarRegalos(deseos);
        assertNotNull(resultado);
        assertEquals(2, resultado.get("tren"));
    }
}