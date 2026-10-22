package reto.dia20;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ControlCalidad {

    // Un juguete inspeccionado: su nivel de defecto va de 0 (perfecto) a 10 (chatarra).
    public record Juguete(String nombre, String categoria, int nivelDefecto, boolean aprobado) {
    }

    // Juguetes aprobados por la inspeccion y con un nivel de defecto dentro del umbral.
    public List<Juguete> filtrarAprobados(List<Juguete> juguetes, int umbralDefecto) {
        if (juguetes == null) {
            return List.of();
        }
        return juguetes.stream()
                .filter(Juguete::aprobado)
                .filter(j -> j.nivelDefecto() <= umbralDefecto)
                .collect(Collectors.toList());
    }

    // Cuenta cuantos juguetes hay de cada categoria.
    public Map<String, Long> contarPorCategoria(List<Juguete> juguetes) {
        if (juguetes == null) {
            return Map.of();
        }
        return juguetes.stream()
                .collect(Collectors.groupingBy(Juguete::categoria, Collectors.counting()));
    }

    // Anomalia: un juguete aprobado que roza la chatarra (defecto alto) no deberia haber pasado.
    public List<Juguete> detectarAnomalias(List<Juguete> juguetes, int defectoCritico) {
        if (juguetes == null) {
            return List.of();
        }
        return juguetes.stream()
                .filter(Juguete::aprobado)
                .filter(j -> j.nivelDefecto() >= defectoCritico)
                .collect(Collectors.toList());
    }
}
