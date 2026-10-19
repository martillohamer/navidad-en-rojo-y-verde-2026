package reto.dia05;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Dia05Test {

    @Test
    void testSimulacionNormal() {
        CintaTransportadora cinta = new CintaTransportadora();
        List<Integer> pesos = Arrays.asList(10, 20, 15);
        assertEquals(45, cinta.simularCinta(pesos, 100));
    }

    @Test
    void testExcepcionCapacidadSuperada() {
        CintaTransportadora cinta = new CintaTransportadora();
        List<Integer> pesos = Arrays.asList(10, 50, 40);
        assertThrows(ConveyorCapacityException.class, () -> {
            cinta.simularCinta(pesos, 60);
        });
    }

    @Test
    void testOmitirCerosConContinue() {
        CintaTransportadora cinta = new CintaTransportadora();
        List<Integer> pesos = Arrays.asList(10, 0, 20);
        assertEquals(30, cinta.simularCinta(pesos, 100));
    }
}