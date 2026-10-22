package reto.dia21;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AlertaTormenta {

    // Una medicion de una estacion: sector, viento en km/h y visibilidad en km.
    public record Medicion(String sector, double velocidadViento, double visibilidadKm) {

        // Peligrosidad: mas viento y menos visibilidad = mas peligro. Un solo numero para ordenar.
        public double peligrosidad() {
            return velocidadViento + (100.0 - visibilidadKm);
        }
    }

    private final double umbralRiesgo;

    public AlertaTormenta(double umbralRiesgo) {
        if (umbralRiesgo < 0) {
            throw new IllegalArgumentException("El umbral de riesgo no puede ser negativo: " + umbralRiesgo);
        }
        this.umbralRiesgo = umbralRiesgo;
    }

    // Sectores en riesgo: viento por encima del umbral O visibilidad critica (menos de 1 km).
    public List<Medicion> sectoresEnRiesgo(List<Medicion> mediciones) {
        if (mediciones == null) {
            return List.of();
        }
        return mediciones.stream()
                .filter(m -> m.velocidadViento() > umbralRiesgo || m.visibilidadKm() < 1.0)
                .collect(Collectors.toList());
    }

    // Mediciones de mayor a menor peligrosidad.
    public List<Medicion> ordenarPorPeligrosidad(List<Medicion> mediciones) {
        if (mediciones == null) {
            return List.of();
        }
        return mediciones.stream()
                .sorted(Comparator.comparingDouble(Medicion::peligrosidad).reversed())
                .collect(Collectors.toList());
    }

    // Alerta roja global: hay al menos una estacion con viento huracanado (>= 118 km/h).
    public boolean hayAlertaRojaGlobal(List<Medicion> mediciones) {
        if (mediciones == null) {
            return false;
        }
        return mediciones.stream()
                .anyMatch(m -> m.velocidadViento() >= 118.0);
    }
}
