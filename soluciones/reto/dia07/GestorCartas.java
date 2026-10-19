package reto.dia07;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GestorCartas {

    public List<Carta> ordenarPorUrgenciaDescendente(List<Carta> cartas) {
        List<Carta> copia = new ArrayList<>(cartas); // no tocamos la lista original
        copia.sort(Comparator.comparingInt(Carta::getUrgencia).reversed());
        return copia;
    }
}