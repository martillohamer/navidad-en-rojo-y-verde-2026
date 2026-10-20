package reto.dia03;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Dia03Test {

    @Test
    void debeHacerCuentaAtrasNormal() {
        RetoDia03 reto = new RetoDia03();
        assertEquals("5, 4, 3, 2, 1, ¡Navidad!", reto.cuentaAtras(5));
    }

    @Test
    void debeManejarCeroONegativos() {
        RetoDia03 reto = new RetoDia03();
        assertEquals("¡Navidad!", reto.cuentaAtras(0));
        assertEquals("¡Navidad!", reto.cuentaAtras(-3));
    }
}
