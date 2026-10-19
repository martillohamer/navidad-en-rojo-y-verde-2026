package reto.dia12;

import java.util.List;

public class GeneradorInformes {

    public int sumarProduccionesAltas(List<Integer> producciones, int umbral) {
        if (producciones == null) return 0;
        return producciones.stream()
                .filter(p -> p > umbral)
                .mapToInt(Integer::intValue)
                .sum();
    }
}