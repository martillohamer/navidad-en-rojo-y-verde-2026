package reto.dia07;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class GestorCartas {

    public List<Carta> ordenarPorUrgenciaDescendente(List<Carta> cartas) {
        Objects.requireNonNull(cartas, "cartas no puede ser null");
        List<Carta> copia = new ArrayList<>(cartas.size()); // no tocamos la lista original
        for (Carta carta : cartas) {
            copia.add(Objects.requireNonNull(carta, "carta no puede ser null"));
        }
        copia.sort(Comparator.comparingInt(Carta::getUrgencia).reversed());
        return copia;
    }
}