package reto.dia04;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TallerDuendes {

    public int calcularProduccionTotal(List<Integer> producciones) {
        Objects.requireNonNull(producciones, "producciones no puede ser null");
        int total = 0;
        for (Integer p : producciones) {
            Objects.requireNonNull(p, "produccion no puede ser null");
            if (p < 0) {
                throw new InvalidProductionException("Produccion negativa: " + p);
            }
            total = Math.addExact(total, p); // falla en vez de desbordar en silencio
        }
        return total;
    }

    public List<Integer> filtrarAltaProduccion(List<Integer> producciones, int umbral) {
        Objects.requireNonNull(producciones, "producciones no puede ser null");
        List<Integer> resultado = new ArrayList<>();
        for (Integer p : producciones) {
            Objects.requireNonNull(p, "produccion no puede ser null");
            if (p > umbral) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}