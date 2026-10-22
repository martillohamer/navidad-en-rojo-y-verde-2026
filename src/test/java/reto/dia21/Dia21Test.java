package reto.dia21;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Dia21Test {

    private List<AlertaTormenta.Medicion> mediciones() {
        return List.of(
                new AlertaTormenta.Medicion("norte", 20.0, 10.0),
                new AlertaTormenta.Medicion("sur", 80.0, 5.0),
                new AlertaTormenta.Medicion("este", 40.0, 0.5),
                new AlertaTormenta.Medicion("oeste", 60.0, 8.0));
    }

    @Test
    void testListaNulaNoFalla() {
        AlertaTormenta alerta = new AlertaTormenta(50.0);
        assertEquals(List.of(), alerta.sectoresEnRiesgo(null));
        assertEquals(List.of(), alerta.ordenarPorPeligrosidad(null));
        assertFalse(alerta.hayAlertaRojaGlobal(null));
    }

    @Test
    void testListaVaciaNoFalla() {
        AlertaTormenta alerta = new AlertaTormenta(50.0);
        assertEquals(List.of(), alerta.sectoresEnRiesgo(List.of()));
        assertTrue(alerta.ordenarPorPeligrosidad(List.of()).isEmpty());
        assertFalse(alerta.hayAlertaRojaGlobal(List.of()));
    }

    @Test
    void testSectoresEnRiesgoPorVientoOVisibilidad() {
        AlertaTormenta alerta = new AlertaTormenta(50.0);
        // sur(80 > 50) y este(visibilidad 0.5 < 1). El oeste(60 > 50) tambien entra.
        List<AlertaTormenta.Medicion> riesgo = alerta.sectoresEnRiesgo(mediciones());
        assertEquals(3, riesgo.size());
    }

    @Test
    void testUmbralExactoNoSeConsideraRiesgo() {
        AlertaTormenta alerta = new AlertaTormenta(60.0);
        // Viento == 60 no supera el umbral y la visibilidad es buena: oeste queda fuera.
        List<AlertaTormenta.Medicion> riesgo = alerta.sectoresEnRiesgo(
                List.of(new AlertaTormenta.Medicion("oeste", 60.0, 8.0)));
        assertEquals(List.of(), riesgo);
    }

    @Test
    void testOrdenarPorPeligrosidad() {
        AlertaTormenta alerta = new AlertaTormenta(50.0);
        List<AlertaTormenta.Medicion> ordenadas = alerta.ordenarPorPeligrosidad(mediciones());
        // este: 40 + 99.5 = 139.5, sur: 80 + 95 = 175, oeste: 60 + 92 = 152, norte: 20 + 90 = 110.
        assertEquals("sur", ordenadas.get(0).sector());
        assertEquals("oeste", ordenadas.get(1).sector());
        assertEquals("este", ordenadas.get(2).sector());
        assertEquals("norte", ordenadas.get(3).sector());
    }

    @Test
    void testAlertaRojaGlobalConVientoHuracanado() {
        AlertaTormenta alerta = new AlertaTormenta(50.0);
        List<AlertaTormenta.Medicion> huracan = List.of(
                new AlertaTormenta.Medicion("norte", 130.0, 3.0));
        assertTrue(alerta.hayAlertaRojaGlobal(huracan));
    }

    @Test
    void testSinAlertaRojaPorDebajoDelLimiteHuracanado() {
        AlertaTormenta alerta = new AlertaTormenta(50.0);
        assertFalse(alerta.hayAlertaRojaGlobal(mediciones()));
    }

    @Test
    void testUmbralNegativoEsInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new AlertaTormenta(-1.0));
    }
}
