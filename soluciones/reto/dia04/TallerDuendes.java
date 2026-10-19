package reto.dia04;

import java.util.ArrayList;
import java.util.List;

public class TallerDuendes {

    public int calcularProduccionTotal(List<Integer> producciones) {
        int total = 0;
        for (int p : producciones) {
            if (p < 0) {
                throw new InvalidProductionException("Produccion negativa: " + p);
            }
            total += p;
        }
        return total;
    }

    public List<Integer> filtrarAltaProduccion(List<Integer> producciones, int umbral) {
        List<Integer> resultado = new ArrayList<>();
        for (int p : producciones) {
            if (p > umbral) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}