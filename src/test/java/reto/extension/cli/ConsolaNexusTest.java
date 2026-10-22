package reto.extension.cli;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import reto.extension.caos.GrinchChaosEngine;
import reto.extension.websocket.TelemetryService;

class ConsolaNexusTest {

    private static ConsolaNexus consola(ByteArrayOutputStream salida) {
        return new ConsolaNexus(new PrintStream(salida, true, StandardCharsets.UTF_8),
                new GrinchChaosEngine(0.0, GrinchChaosEngine.PoliticaCaos.limpia(), new Random(42L)),
                TelemetryService.conSemilla(42L));
    }

    private static String texto(ByteArrayOutputStream salida) {
        return salida.toString(StandardCharsets.UTF_8);
    }

    @Test
    void testParsearArgumentosPosicionalesYConValor() {
        ArgumentosCli args = ArgumentosCli.parsear("status --agentes=7 --verbose");
        assertEquals(java.util.List.of("status"), args.posicionales());
        assertEquals(7, args.opcionEntera("agentes", 0));
        assertTrue(args.bandera("verbose"));
        assertEquals("true", args.opcion("verbose").orElseThrow());
        assertFalse(args.bandera("inexistente"));
    }

    @Test
    void testParsearArgumentosVacios() {
        ArgumentosCli args = ArgumentosCli.parsear("   ");
        assertTrue(args.posicionales().isEmpty());
        assertTrue(args.opciones().isEmpty());
        assertEquals(0, args.tamano());
    }

    @Test
    void testOpcionEnteraInvalidaLanzaExcepcion() {
        ArgumentosCli args = ArgumentosCli.parsear("status --agentes=muchos");
        assertThrows(IllegalArgumentException.class, () -> args.opcionEntera("agentes", 1));
    }

    @Test
    void testOpcionDecimalConPorDefecto() {
        assertEquals(2.5, ArgumentosCli.parsear("chaos").opcionDecimal("p", 2.5));
        assertEquals(0.7, ArgumentosCli.parsear("chaos --p=0.7").opcionDecimal("p", 0.0));
    }

    @Test
    void testComandoParseaNombreYResto() {
        ComandoCli comando = ComandoCli.parsear("status --agentes=3");
        assertEquals("status", comando.nombre());
        assertEquals(3, comando.argumentos().opcionEntera("agentes", 0));
    }

    @Test
    void testComandoSinNombreFalla() {
        assertThrows(IllegalArgumentException.class, () -> ComandoCli.parsear("   "));
    }

    @Test
    void testComandoNormalizaMayusculas() {
        ComandoCli comando = ComandoCli.parsear("STATUS");
        assertEquals("status", comando.nombre());
    }

    @Test
    void testRespuestaOkYError() {
        assertTrue(RespuestaCli.ok("bien").esExito());
        assertFalse(RespuestaCli.error("mal").esExito());
        assertEquals(RespuestaCli.EXITO, RespuestaCli.ok("bien").codigo());
        assertEquals(RespuestaCli.ERROR_USO, RespuestaCli.error("mal").codigo());
    }

    @Test
    void testStatusMuestraLosAgentes() {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        RespuestaCli respuesta = consola(salida).ejecutar("status --agentes=3");
        assertTrue(respuesta.esExito());
        String txt = texto(salida);
        assertTrue(txt.contains("agente-1"));
        assertTrue(txt.contains("agente-3"));
        assertFalse(txt.contains("agente-4"));
        assertTrue(txt.contains("total=3"));
    }

    @Test
    void testStatusRechazaAgentesInvalidos() {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        RespuestaCli respuesta = consola(salida).ejecutar("status --agentes=0");
        assertFalse(respuesta.esExito());
        assertTrue(respuesta.mensaje().contains("positivo"));
    }

    @Test
    void testChaosAlcanzaConsensoConPoliticaLimpia() {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        RespuestaCli respuesta = consola(salida).ejecutar("chaos --nodos=5 --requeridos=3");
        assertTrue(respuesta.esExito());
        String txt = texto(salida);
        assertTrue(txt.contains("nodos=5"));
        assertTrue(txt.contains("CONSENSO ALCANZADO"));
    }

    @Test
    void testChaosConRequeridosInvalidoDevuelveError() {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        RespuestaCli respuesta = consola(salida).ejecutar("chaos --nodos=3 --requeridos=9");
        assertFalse(respuesta.esExito());
    }

    @Test
    void testBftVerificaLedgerIntegro() {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        RespuestaCli respuesta = consola(salida).ejecutar("bft");
        assertTrue(respuesta.esExito());
        String txt = texto(salida);
        assertTrue(txt.contains("LEDGER INTEGRO"));
        assertTrue(txt.contains("raiz="));
    }

    @Test
    void testTelemetriaEmiteLasMuestrasPedidas() {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        RespuestaCli respuesta = consola(salida).ejecutar("telemetria --n=2");
        assertTrue(respuesta.esExito());
        String txt = texto(salida);
        assertTrue(txt.contains("Telemetria de agentes"));
        long lineas = txt.lines().filter(l -> l.contains("=")).count();
        assertTrue(lineas >= 2);
    }

    @Test
    void testTelemetriaConCantidadInvalidaFalla() {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        RespuestaCli respuesta = consola(salida).ejecutar("telemetria --n=0");
        assertFalse(respuesta.esExito());
    }

    @Test
    void testHelpListaLosComandos() {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        RespuestaCli respuesta = consola(salida).ejecutar("help");
        assertTrue(respuesta.esExito());
        String txt = texto(salida);
        for (String comando : ConsolaNexus.COMANDOS) {
            assertTrue(txt.contains(comando), "help debe mencionar " + comando);
        }
    }

    @Test
    void testComandoDesconocidoDevuelveError() {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        RespuestaCli respuesta = consola(salida).ejecutar("volar");
        assertFalse(respuesta.esExito());
        assertTrue(respuesta.mensaje().contains("desconocido"));
    }

    @Test
    void testEjecutarLineaVaciaDevuelveError() {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        RespuestaCli respuesta = consola(salida).ejecutar("   ");
        assertFalse(respuesta.esExito());
    }

    @Test
    void testBucleProcesaComandosHastaExit() throws Exception {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        String entrada = "status --agentes=2\nhelp\nexit\nstatus --agentes=9\n";
        ConsolaNexus consola = consola(salida);
        consola.bucle(new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)));
        String txt = texto(salida);
        assertTrue(txt.contains("agente-2"));
        assertTrue(txt.contains("Manual de la Consola Nexus"));
        assertFalse(txt.contains("agente-9"), "el bucle debe detenerse al recibir exit");
    }
}
