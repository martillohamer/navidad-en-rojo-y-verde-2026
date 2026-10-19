package reto.dia07;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class Dia07Test {

    @Test
    void testOrdenacionUrgencia() {
        GestorCartas gestor = new GestorCartas();
        List<Carta> cartas = Arrays.asList(
            new Carta("Pedro", 2),
            new Carta("Maria", 5),
            new Carta("Juan", 3)
        );

        List<Carta> ordenadas = gestor.ordenarPorUrgenciaDescendente(cartas);
        assertEquals("Maria", ordenadas.get(0).getNino());
        assertEquals("Juan", ordenadas.get(1).getNino());
        assertEquals("Pedro", ordenadas.get(2).getNino());
    }
}