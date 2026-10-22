package reto.dia05;

import java.util.List;
import java.util.Objects;

public class CintaTransportadora {

    public int simularCinta(List<Integer> pesosPaquetes, int capacidadMaxima) {
        Objects.requireNonNull(pesosPaquetes, "pesosPaquetes no puede ser null");
        if (capacidadMaxima < 0) {
            throw new IllegalArgumentException("La capacidad maxima no puede ser negativa: " + capacidadMaxima);
        }
        int total = 0;
        for (Integer peso : pesosPaquetes) {
            Objects.requireNonNull(peso, "peso no puede ser null");
            if (peso == 0) {
                continue; // un paquete vacio no cuenta
            }
            if (peso < 0) {
                throw new IllegalArgumentException("El peso no puede ser negativo: " + peso);
            }
            total = Math.addExact(total, peso); // falla en vez de desbordar en silencio
            if (total > capacidadMaxima) {
                throw new ConveyorCapacityException("Capacidad superada: " + total + " > " + capacidadMaxima);
            }
        }
        return total;
    }
}