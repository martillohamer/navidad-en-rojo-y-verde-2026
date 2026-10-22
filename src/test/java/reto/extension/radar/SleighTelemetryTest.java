package reto.extension.radar;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SleighTelemetryTest {

    private final SleighTelemetryServer servidor = new SleighTelemetryServer();

    @AfterEach
    void limpiar() {
        servidor.detener();
    }

    @Test
    void testEmitirLatidoAvanzaLaPosicion() {
        SleighTelemetryServer.Telemetria t = servidor.emitirLatido();
        assertEquals(0.5, t.latitud(), 1e-9);
        assertEquals(0.3, t.longitud(), 1e-9);
        assertEquals(10.0, t.velocidadKmh(), 1e-9);
        assertEquals("ALTA", t.estadoBateria());
    }

    @Test
    void testEstadoBateriaCambiaConLaVelocidad() {
        SleighTelemetryServer.Telemetria ultima = null;
        for (int i = 0; i < 30; i++) {
            ultima = servidor.emitirLatido();
        }
        assertEquals("BAJA", ultima.estadoBateria());
        assertEquals(300.0, ultima.velocidadKmh(), 1e-9);
    }

    @Test
    void testHistorialAcumulaLatidos() {
        servidor.emitirLatido();
        servidor.emitirLatido();
        servidor.emitirLatido();
        assertEquals(3, servidor.totalLatidos());
        List<SleighTelemetryServer.Telemetria> copia = servidor.historial();
        assertEquals(3, copia.size());
    }

    @Test
    void testIniciarActivaElServidor() {
        servidor.iniciar(10);
        assertTrue(servidor.activo());
    }

    @Test
    void testIniciarConPeriodoInvalidoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> servidor.iniciar(0));
        assertThrows(IllegalArgumentException.class, () -> servidor.iniciar(-5));
    }

    @Test
    void testEmiteEventosPeriodicosYSeDetieneLimpiamente() throws InterruptedException {
        servidor.iniciar(20);
        Thread.sleep(150);
        servidor.detener();
        assertFalse(servidor.activo());
        int latidos = servidor.totalLatidos();
        assertTrue(latidos >= 1, "Debe haber emitido al menos un latido, hubo: " + latidos);
        Thread.sleep(60);
        assertEquals(latidos, servidor.totalLatidos(), "No debe emitir tras detener()");
    }

    @Test
    void testIniciarDosVecesNoDuplicaElPlanificador() throws InterruptedException {
        servidor.iniciar(30);
        servidor.iniciar(30);
        Thread.sleep(100);
        servidor.detener();
        assertFalse(servidor.activo());
    }

    @Test
    void testDetenerSinIniciarNoFalla() {
        servidor.detener();
        assertFalse(servidor.activo());
    }

    @Test
    void testEsperarTerminacionTrasDetenerDevuelveTrue() throws InterruptedException {
        servidor.iniciar(20);
        Thread.sleep(50);
        servidor.detener();
        assertTrue(servidor.esperarTerminacion(500));
    }
}
