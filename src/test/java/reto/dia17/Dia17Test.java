package reto.dia17;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Dia17Test {

    private final OptimizadorRutas optimizador = new OptimizadorRutas();

    @Test
    void testRutaNula() {
        assertEquals(0.0, optimizador.calcularDistanciaTotal(null));
    }

    @Test
    void testRutaVacia() {
        assertEquals(0.0, optimizador.calcularDistanciaTotal(List.of()));
    }

    @Test
    void testUnSoloPuntoHaceIdaYVuelta() {
        // Ir de (0,0) a (3,4) y volver: 5 + 5 = 10.
        List<OptimizadorRutas.Punto> paradas = List.of(new OptimizadorRutas.Punto(3, 4));
        assertEquals(10.0, optimizador.calcularDistanciaTotal(paradas), 1e-9);
    }

    @Test
    void testVariasParadasConRetornoAlOrigen() {
        // (0,0) -> (3,0) -> (3,4) -> (0,0): 3 + 4 + 5 = 12.
        List<OptimizadorRutas.Punto> paradas = List.of(
                new OptimizadorRutas.Punto(3, 0),
                new OptimizadorRutas.Punto(3, 4));
        assertEquals(12.0, optimizador.calcularDistanciaTotal(paradas), 1e-9);
    }

    @Test
    void testParadaEnElOrigenNoSumaDistancia() {
        // (0,0) -> (0,0) -> (0,0): no hay desplazamiento.
        List<OptimizadorRutas.Punto> paradas = List.of(new OptimizadorRutas.Punto(0, 0));
        assertEquals(0.0, optimizador.calcularDistanciaTotal(paradas), 1e-9);
    }
}
