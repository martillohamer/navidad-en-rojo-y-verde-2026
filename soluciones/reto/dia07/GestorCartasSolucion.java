package reto.dia07;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GestorCartas {

    public List<Carta> ordenarPorUrgenciaDescendente(List<Carta> cartas) {
        List<Carta> resultado = new ArrayList<>(cartas);
        resultado.sort(Comparator.comparingInt(Carta::getUrgencia).reversed());
        return resultado;
    }
}