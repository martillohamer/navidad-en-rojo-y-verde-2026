package reto.dia05;

import java.util.List;

public class CintaTransportadora {

    public int simularCinta(List<Integer> pesosPaquetes, int capacidadMaxima) {
        int total = 0;
        for (int peso : pesosPaquetes) {
            if (peso == 0) {
                continue; // un paquete vacio no cuenta
            }
            total += peso;
            if (total > capacidadMaxima) {
                throw new ConveyorCapacityException("Capacidad superada: " + total + " > " + capacidadMaxima);
            }
        }
        return total;
    }
}