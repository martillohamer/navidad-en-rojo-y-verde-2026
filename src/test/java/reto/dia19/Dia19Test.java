package reto.dia19;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Dia19Test {

    @Test
    void testListaNulaDevuelveCero() {
        CalculadorCarga calculador = new CalculadorCarga(100.0);
        assertEquals(0.0, calculador.calcularPesoTotal(null), 1e-9);
    }

    @Test
    void testListaVaciaDevuelveCero() {
        CalculadorCarga calculador = new CalculadorCarga(100.0);
        assertEquals(0.0, calculador.calcularPesoTotal(List.of()), 1e-9);
    }

    @Test
    void testCargaDentroDelLimite() {
        CalculadorCarga calculador = new CalculadorCarga(50.0);
        List<CalculadorCarga.Regalo> regalos = List.of(
                new CalculadorCarga.Regalo("tren", 10.0),
                new CalculadorCarga.Regalo("muneca", 5.5),
                new CalculadorCarga.Regalo("libro", 2.5));
        assertEquals(18.0, calculador.calcularPesoTotal(regalos), 1e-9);
    }

    @Test
    void testPesoExactoAlLimiteNoLanzaExcepcion() {
        CalculadorCarga calculador = new CalculadorCarga(15.0);
        List<CalculadorCarga.Regalo> regalos = List.of(
                new CalculadorCarga.Regalo("tren", 10.0),
                new CalculadorCarga.Regalo("muneca", 5.0));
        assertEquals(15.0, calculador.calcularPesoTotal(regalos), 1e-9);
    }

    @Test
    void testExcesoDeCapacidadLanzaExcepcion() {
        CalculadorCarga calculador = new CalculadorCarga(20.0);
        List<CalculadorCarga.Regalo> regalos = List.of(
                new CalculadorCarga.Regalo("tren", 10.0),
                new CalculadorCarga.Regalo("bici", 15.0));
        assertThrows(ExcesoCapacidadException.class, () -> calculador.calcularPesoTotal(regalos));
    }

    @Test
    void testPesoMaximoNegativoEsInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new CalculadorCarga(-1.0));
    }
}
