package reto.dia04;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Dia04Test {

    @Test
    void testCalcularProduccionTotalValida() {
        TallerDuendes taller = new TallerDuendes();
        List<Integer> prod = Arrays.asList(10, 20, 15, 30);
        assertEquals(75, taller.calcularProduccionTotal(prod));
    }

    @Test
    void testProduccionNegativaLanzaExcepcion() {
        TallerDuendes taller = new TallerDuendes();
        List<Integer> prod = Arrays.asList(10, -5, 15);
        assertThrows(InvalidProductionException.class, () -> {
            taller.calcularProduccionTotal(prod);
        });
    }

    @Test
    void testFiltrarAltaProduccion() {
        TallerDuendes taller = new TallerDuendes();
        List<Integer> prod = Arrays.asList(5, 25, 12, 30, 18);
        List<Integer> filtradas = taller.filtrarAltaProduccion(prod, 15);
        assertEquals(Arrays.asList(25, 30, 18), filtradas);
    }
}