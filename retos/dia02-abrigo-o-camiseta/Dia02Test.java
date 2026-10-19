package reto.dia02;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Dia02Test {

    @Test
    void debeRecomendarAbrigoParaFrio() {
        RetoDia02 reto = new RetoDia02();
        assertEquals("abrigo", reto.ropaSegunTemperatura(5));
    }

    @Test
    void debeRecomendarCamisetaParaCalor() {
        RetoDia02 reto = new RetoDia02();
        assertEquals("camiseta", reto.ropaSegunTemperatura(35));
    }
}
