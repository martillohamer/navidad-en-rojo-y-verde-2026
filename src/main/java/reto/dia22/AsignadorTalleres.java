package reto.dia22;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AsignadorTalleres {

    // Un elfo de la plantilla: su nombre, su taller (especialidad) y su experiencia.
    public record Elfo(String nombre, String especialidad, int experienciaAnios) {
    }

    // Agrupa los elfos por taller.
    public Map<String, List<Elfo>> agruparPorEspecialidad(List<Elfo> elfos) {
        if (elfos == null) {
            return Map.of();
        }
        return elfos.stream()
                .collect(Collectors.groupingBy(Elfo::especialidad));
    }

    // El elfo mas experimentado de cada taller.
    public Map<String, Optional<Elfo>> masExperimentadoPorTaller(List<Elfo> elfos) {
        if (elfos == null) {
            return Map.of();
        }
        return elfos.stream()
                .collect(Collectors.groupingBy(
                        Elfo::especialidad,
                        Collectors.maxBy(Comparator.comparingInt(Elfo::experienciaAnios))));
    }

    // Reparte los elfos en "numeroTalleres" grupos lo mas equilibrados posible (round-robin).
    public Map<Integer, List<Elfo>> distribuirEquilibrado(List<Elfo> elfos, int numeroTalleres) {
        if (elfos == null || elfos.isEmpty() || numeroTalleres <= 0) {
            return Map.of();
        }
        Map<Integer, List<Elfo>> grupos = new LinkedHashMap<>();
        for (int i = 0; i < numeroTalleres; i++) {
            grupos.put(i, new ArrayList<>());
        }
        for (int i = 0; i < elfos.size(); i++) {
            grupos.get(i % numeroTalleres).add(elfos.get(i));
        }
        return grupos;
    }
}
