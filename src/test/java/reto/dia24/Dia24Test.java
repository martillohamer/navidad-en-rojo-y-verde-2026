package reto.dia24;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import reto.dia17.OptimizadorRutas;
import reto.dia19.CalculadorCarga;
import reto.dia21.AlertaTormenta;

class Dia24Test {

    private final SimuladorNavidad simulador = new SimuladorNavidad(100.0, 60.0);

    private List<CalculadorCarga.Regalo> cargaLigera() {
        return List.of(
                new CalculadorCarga.Regalo("tren", 10.0),
                new CalculadorCarga.Regalo("muneca", 5.0));
    }

    private List<CalculadorCarga.Regalo> cargaExcesiva() {
        return List.of(
                new CalculadorCarga.Regalo("tren", 80.0),
                new CalculadorCarga.Regalo("bici", 50.0));
    }

    private List<OptimizadorRutas.Punto> rutaValida() {
        return List.of(
                new OptimizadorRutas.Punto(3, 0),
                new OptimizadorRutas.Punto(3, 4));
    }

    private List<AlertaTormenta.Medicion> climaTranquilo() {
        return List.of(new AlertaTormenta.Medicion("norte", 20.0, 10.0));
    }

    private List<AlertaTormenta.Medicion> climaTormenta() {
        return List.of(new AlertaTormenta.Medicion("norte", 130.0, 3.0));
    }

    @Test
    void testDespegueAutorizadoConTodosLosSistemasEnVerde() {
        SimuladorNavidad.InformeMision informe =
                simulador.simular(cargaLigera(), rutaValida(), climaTranquilo());
        assertTrue(informe.despegueAutorizado());
        assertEquals(15.0, informe.pesoTotal(), 1e-9);
        assertEquals(2, informe.totalParadas());
        assertEquals("FAVORABLE", informe.estadoClimatico());
    }

    @Test
    void testExcesoDePesoAbortaElDespegue() {
        SimuladorNavidad.InformeMision informe =
                simulador.simular(cargaExcesiva(), rutaValida(), climaTranquilo());
        assertFalse(informe.despegueAutorizado());
        assertEquals(130.0, informe.pesoTotal(), 1e-9);
    }

    @Test
    void testTormentaCriticaAbortaElDespegue() {
        SimuladorNavidad.InformeMision informe =
                simulador.simular(cargaLigera(), rutaValida(), climaTormenta());
        assertFalse(informe.despegueAutorizado());
        assertEquals("CRITICO", informe.estadoClimatico());
    }

    @Test
    void testRutaVaciaAbortaElDespegue() {
        SimuladorNavidad.InformeMision informe =
                simulador.simular(cargaLigera(), List.of(), climaTranquilo());
        assertFalse(informe.despegueAutorizado());
        assertEquals(0, informe.totalParadas());
    }

    @Test
    void testRutaNulaAbortaElDespegueSinFallar() {
        SimuladorNavidad.InformeMision informe =
                simulador.simular(cargaLigera(), null, climaTranquilo());
        assertFalse(informe.despegueAutorizado());
        assertEquals(0, informe.totalParadas());
    }

    @Test
    void testCargaNulaSeTrataComoCero() {
        SimuladorNavidad.InformeMision informe =
                simulador.simular(null, rutaValida(), climaTranquilo());
        assertTrue(informe.despegueAutorizado());
        assertEquals(0.0, informe.pesoTotal(), 1e-9);
    }
}
