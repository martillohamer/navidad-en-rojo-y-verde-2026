package reto.dia08;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class Dia08Test {

    @Test
    void testJornadaExitoso() {
        MotorPoloNorte motor = new MotorPoloNorte();
        boolean res = motor.validarJornadaCompleta(Arrays.asList(10, 20), 100, Arrays.asList(15, 25));
        assertTrue(res);
    }
}