package reto.dia20;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Dia20Test {

    private final ControlCalidad control = new ControlCalidad();

    private List<ControlCalidad.Juguete> juguetes() {
        return List.of(
                new ControlCalidad.Juguete("tren", "vehiculos", 1, true),
                new ControlCalidad.Juguete("muneca", "peluches", 0, true),
                new ControlCalidad.Juguete("coche", "vehiculos", 8, true),
                new ControlCalidad.Juguete("oso", "peluches", 3, false),
                new ControlCalidad.Juguete("puzzle", "juegos", 2, true));
    }

    @Test
    void testListaNulaNoFalla() {
        assertEquals(List.of(), control.filtrarAprobados(null, 5));
        assertEquals(Map.of(), control.contarPorCategoria(null));
        assertEquals(List.of(), control.detectarAnomalias(null, 7));
    }

    @Test
    void testListaVaciaNoFalla() {
        assertEquals(List.of(), control.filtrarAprobados(List.of(), 5));
        assertTrue(control.contarPorCategoria(List.of()).isEmpty());
        assertEquals(List.of(), control.detectarAnomalias(List.of(), 7));
    }

    @Test
    void testFiltrarAprobadosPorUmbral() {
        // Aprobados y con defecto <= 5: tren(1), muneca(0), puzzle(2). El coche(8) queda fuera.
        List<ControlCalidad.Juguete> resultado = control.filtrarAprobados(juguetes(), 5);
        assertEquals(3, resultado.size());
        assertTrue(resultado.stream().allMatch(ControlCalidad.Juguete::aprobado));
        assertTrue(resultado.stream().allMatch(j -> j.nivelDefecto() <= 5));
    }

    @Test
    void testContarPorCategoria() {
        Map<String, Long> conteo = control.contarPorCategoria(juguetes());
        assertEquals(2L, conteo.get("vehiculos"));
        assertEquals(2L, conteo.get("peluches"));
        assertEquals(1L, conteo.get("juegos"));
    }

    @Test
    void testDetectarAnomalias() {
        // Un aprobado con defecto >= 7 es sospechoso: solo el coche(8).
        List<ControlCalidad.Juguete> anomalias = control.detectarAnomalias(juguetes(), 7);
        assertEquals(1, anomalias.size());
        assertEquals("coche", anomalias.get(0).nombre());
    }
}
