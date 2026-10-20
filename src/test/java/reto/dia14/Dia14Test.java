package reto.dia14;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Dia14Test {

    @Test
    void testExtraerStockTotal() {
        ParserInventario parser = new ParserInventario();
        String csv = "tren,150,juguetes";
        assertEquals(150, parser.extraerStockTotal(csv));
    }
}