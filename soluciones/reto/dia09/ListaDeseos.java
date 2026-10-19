package reto.dia09;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListaDeseos {

    public Map<String, Integer> contarRegalos(List<String> deseos) {
        Map<String, Integer> conteo = new HashMap<>();
        for (String deseo : deseos) {
            conteo.merge(deseo, 1, Integer::sum);
        }
        return conteo;
    }
}