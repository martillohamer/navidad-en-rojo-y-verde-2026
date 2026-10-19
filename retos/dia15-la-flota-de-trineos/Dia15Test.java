package reto.dia15;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Dia15Test {

    @Test
    void testVelocidadTrineoMagico() {
        Trineo trineo = new TrineoMagico(50);
        assertEquals(100, trineo.calcularVelocidadMax());
    }
}