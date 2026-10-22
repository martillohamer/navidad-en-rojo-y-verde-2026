package reto.dia12;

import java.util.List;

public class GeneradorInformes {

    public int sumarProduccionesAltas(List<Integer> producciones, int umbral) {
        return producciones.stream()
                .filter(p -> p > umbral)
                .mapToInt(Integer::intValue)
                .sum();
    }
}