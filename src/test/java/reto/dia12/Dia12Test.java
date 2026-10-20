package reto.dia12;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class Dia12Test {

    @Test
    void testSumarProduccionesAltasStream() {
        GeneradorInformes gen = new GeneradorInformes();
        List<Integer> prod = Arrays.asList(5, 20, 15, 30, 8);
        int suma = gen.sumarProduccionesAltas(prod, 10);
        // Valores > 10 son 20, 15, 30 -> suma = 65
        assertEquals(65, suma);
    }
}