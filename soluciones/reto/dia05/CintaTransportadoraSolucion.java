package reto.dia05;

import java.util.List;

public class CintaTransportadora {

    public int simularCinta(List<Integer> pesosPaquetes, int capacidadMaxima) {
        int pesoAcumulado = 0;
        int i = 0;
        
        while (i < pesosPaquetes.size()) {
            int peso = pesosPaquetes.get(i++);
            
            if (peso == 0) {
                continue; // Omitir paquetes vacíos
            }
            
            if (pesoAcumulado + peso > capacidadMaxima) {
                throw new ConveyorCapacityException("Capacidad máxima de la cinta superada: " + (pesoAcumulado + peso));
            }
            
            pesoAcumulado += peso;
        }
        
        return pesoAcumulado;
    }
}