package reto.dia09;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListaDeseos {

    public Map<String, Integer> contarRegalos(List<String> deseos) {
        Map<String, Integer> conteo = new HashMap<>();
        for (String deseo : deseos) {
            conteo.put(deseo, conteo.getOrDefault(deseo, 0) + 1);
        }
        return conteo;
    }
}