package reto.dia22;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Dia22Test {

    private List<AsignadorTalleres.Elfo> elfos() {
        return List.of(
                new AsignadorTalleres.Elfo("Ana", "pintura", 3),
                new AsignadorTalleres.Elfo("Bruno", "montaje", 5),
                new AsignadorTalleres.Elfo("Carla", "pintura", 8),
                new AsignadorTalleres.Elfo("Dani", "montaje", 2),
                new AsignadorTalleres.Elfo("Eva", "carpinteria", 6));
    }

    @Test
    void testListaNulaNoFalla() {
        AsignadorTalleres asignador = new AsignadorTalleres();
        assertEquals(Map.of(), asignador.agruparPorEspecialidad(null));
        assertEquals(Map.of(), asignador.masExperimentadoPorTaller(null));
        assertEquals(Map.of(), asignador.distribuirEquilibrado(null, 2));
    }

    @Test
    void testListaVaciaNoFalla() {
        AsignadorTalleres asignador = new AsignadorTalleres();
        assertTrue(asignador.agruparPorEspecialidad(List.of()).isEmpty());
        assertTrue(asignador.masExperimentadoPorTaller(List.of()).isEmpty());
        assertTrue(asignador.distribuirEquilibrado(List.of(), 2).isEmpty());
    }

    @Test
    void testAgruparPorEspecialidad() {
        AsignadorTalleres asignador = new AsignadorTalleres();
        Map<String, List<AsignadorTalleres.Elfo>> grupos = asignador.agruparPorEspecialidad(elfos());
        assertEquals(2, grupos.get("pintura").size());
        assertEquals(2, grupos.get("montaje").size());
        assertEquals(1, grupos.get("carpinteria").size());
    }

    @Test
    void testMasExperimentadoPorTaller() {
        AsignadorTalleres asignador = new AsignadorTalleres();
        Map<String, java.util.Optional<AsignadorTalleres.Elfo>> expertos =
                asignador.masExperimentadoPorTaller(elfos());
        assertEquals("Carla", expertos.get("pintura").orElseThrow().nombre());
        assertEquals("Bruno", expertos.get("montaje").orElseThrow().nombre());
        assertEquals("Eva", expertos.get("carpinteria").orElseThrow().nombre());
    }

    @Test
    void testDistribucionEquilibrada() {
        AsignadorTalleres asignador = new AsignadorTalleres();
        Map<Integer, List<AsignadorTalleres.Elfo>> grupos = asignador.distribuirEquilibrado(elfos(), 2);
        // 5 elfos en 2 grupos: el 3 y el 2, sin perder ninguno.
        assertEquals(3, grupos.get(0).size());
        assertEquals(2, grupos.get(1).size());
        assertEquals(5, grupos.get(0).size() + grupos.get(1).size());
    }

    @Test
    void testNumeroTalleresInvalidoDevuelveVacio() {
        AsignadorTalleres asignador = new AsignadorTalleres();
        assertTrue(asignador.distribuirEquilibrado(elfos(), 0).isEmpty());
        assertTrue(asignador.distribuirEquilibrado(elfos(), -1).isEmpty());
    }
}
