package reto.dia06;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Dia06Test {

    @Test
    void testRegaloLiviano() {
        TrineoSanta trineo = new TrineoSanta();
        assertEquals("LIVIANO-ALTA", trineo.determinarCategoriaYPrioridad(1.5, true));
        assertEquals("LIVIANO-NORMAL", trineo.determinarCategoriaYPrioridad(1.5, false));
    }

    @Test
    void testRegaloPesado() {
        TrineoSanta trineo = new TrineoSanta();
        assertEquals("PESADO-URGENTE", trineo.determinarCategoriaYPrioridad(15.0, true));
        assertEquals("PESADO-NORMAL", trineo.determinarCategoriaYPrioridad(15.0, false));
    }
}