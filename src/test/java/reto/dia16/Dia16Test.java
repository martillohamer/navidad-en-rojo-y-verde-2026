package reto.dia16;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Dia16Test {

    @Test
    void testEvaluarComportamientoPremio() {
        MotorReglas motor = new MotorReglas();
        String resultado = motor.evaluarComportamiento(3, true);
        assertEquals("REGALO", resultado);
    }
}