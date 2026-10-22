package reto.dia19;

import java.util.List;

public class CalculadorCarga {

    // Un regalo tiene nombre y peso en kilogramos.
    public record Regalo(String nombre, double peso) {
    }

    private final double pesoMaximo;

    public CalculadorCarga(double pesoMaximo) {
        if (pesoMaximo < 0) {
            throw new IllegalArgumentException("El peso maximo no puede ser negativo: " + pesoMaximo);
        }
        this.pesoMaximo = pesoMaximo;
    }

    // Suma el peso de todos los regalos y vigila no pasarse del limite del trineo.
    public double calcularPesoTotal(List<Regalo> regalos) {
        if (regalos == null || regalos.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (Regalo regalo : regalos) {
            total += regalo.peso();
            if (total > pesoMaximo) {
                throw new ExcesoCapacidadException(
                        "Capacidad superada: " + total + " kg > " + pesoMaximo + " kg");
            }
        }
        return total;
    }
}
