package reto.dia01;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Dia01Test {

    @Test
    void debeSaludarConNombre() {
        RetoDia01 reto = new RetoDia01();
        assertEquals("¡Feliz Navidad, Papá Noel!", reto.saludoNavideno("Papá Noel"));
    }
}
