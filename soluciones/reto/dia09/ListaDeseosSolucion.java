package reto.dia09;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ListaDeseosSolucion {
    public Map<String, Integer> contarRegalos(List<String> d) {
        Map<String, Integer> m = new HashMap<>();
        if (d != null) {
            for (String s : d) {
                m.put(s, m.getOrDefault(s, 0) + 1);
            }
        }
        return m;
    }
}