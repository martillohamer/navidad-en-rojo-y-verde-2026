package reto.dia11;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Dia11Test {

    @Test
    void testExtraerCodigoPostal() {
        AnalizadorCartas analizador = new AnalizadorCartas();
        String texto = "Hola Santa, vivo en Madrid con CP 28001 y me porto bien.";
        assertEquals("28001", analizador.extraerCodigoPostal(texto));
    }
}