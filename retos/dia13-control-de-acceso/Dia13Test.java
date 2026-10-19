package reto.dia13;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Dia13Test {

    @Test
    void testAccesoDenegado() {
        ControlAcceso control = new ControlAcceso();
        assertThrows(AccesoDenegadoException.class, () -> {
            control.verificarPase("junior", 1);
        });
    }
}