package reto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RetoTest {

    @Test
    void debePasarElReto() {
        Reto reto = new Reto();
        assertThrows(UnsupportedOperationException.class, () -> {
            reto.resolver();
        }, "El reto debe empezar fallando en rojo (lanzando UnsupportedOperationException)");
    }
}
