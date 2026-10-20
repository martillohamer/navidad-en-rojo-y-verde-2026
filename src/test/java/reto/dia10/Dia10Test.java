package reto.dia10;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Dia10Test {

    @Test
    void testMovimientosTrineo() {
        NavegadorTrineo nav = new NavegadorTrineo();
        Posicion pos = nav.calcularDestino("Norte,Norte,Este,Sur");
        assertEquals(1, pos.getX());
        assertEquals(1, pos.getY());
    }
}