package reto.extension.k8s;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ElfoOperatorTest {

    private ElfoOperator.ElfoSpec spec(int replicas) {
        return new ElfoOperator.ElfoSpec("taller", replicas, "v2");
    }

    private ElfoOperator.PodElfo pod(String nombre, String estado) {
        return new ElfoOperator.PodElfo(nombre, estado, 0);
    }

    private ElfoOperator.ElfoRecurso recurso(int replicas, List<ElfoOperator.PodElfo> pods) {
        return new ElfoOperator.ElfoRecurso("taller", spec(replicas), pods);
    }

    @Test
    void testEscalaHaciaArribaCreandoPods() {
        ElfoOperator operador = new ElfoOperator();
        ElfoOperator.Reconciliacion r = operador.reconciliar(
                recurso(3, List.of(pod("taller-0", ElfoOperator.PodElfo.ESTADO_RUNNING))));
        assertTrue(r.cambiosAplicados());
        assertEquals(3, r.estadoResultante().pods().size());
        assertEquals(3, r.estadoResultante().spec().replicas());
        assertTrue(r.acciones().stream().anyMatch(a -> a.startsWith("crear:")));
    }

    @Test
    void testEscalaHaciaAbajoEliminandoPods() {
        ElfoOperator operador = new ElfoOperator();
        ElfoOperator.Reconciliacion r = operador.reconciliar(recurso(1, List.of(
                pod("taller-0", ElfoOperator.PodElfo.ESTADO_RUNNING),
                pod("taller-1", ElfoOperator.PodElfo.ESTADO_RUNNING),
                pod("taller-2", ElfoOperator.PodElfo.ESTADO_RUNNING))));
        assertTrue(r.cambiosAplicados());
        assertEquals(1, r.estadoResultante().pods().size());
        assertTrue(r.acciones().stream().anyMatch(a -> a.startsWith("eliminar:")));
    }

    @Test
    void testAutoReparacionDePodsCaidos() {
        ElfoOperator operador = new ElfoOperator();
        ElfoOperator.Reconciliacion r = operador.reconciliar(recurso(2, List.of(
                pod("taller-0", ElfoOperator.PodElfo.ESTADO_RUNNING),
                pod("taller-1", ElfoOperator.PodElfo.ESTADO_CRASHED))));
        assertTrue(r.cambiosAplicados());
        assertTrue(r.acciones().contains("reiniciar:taller-1"));
        ElfoOperator.PodElfo reparado = r.estadoResultante().pods().get(1);
        assertEquals(ElfoOperator.PodElfo.ESTADO_RUNNING, reparado.estado());
        assertEquals(1, reparado.reinicios());
    }

    @Test
    void testSinAutoReparacionNoReiniciaPodsCaidos() {
        ElfoOperator operador = new ElfoOperator(false);
        ElfoOperator.Reconciliacion r = operador.reconciliar(recurso(1, List.of(
                pod("taller-0", ElfoOperator.PodElfo.ESTADO_CRASHED))));
        assertFalse(r.cambiosAplicados());
        assertEquals(ElfoOperator.PodElfo.ESTADO_CRASHED,
                r.estadoResultante().pods().get(0).estado());
    }

    @Test
    void testSinCambiosCuandoYaEstaConvergido() {
        ElfoOperator operador = new ElfoOperator();
        ElfoOperator.Reconciliacion r = operador.reconciliar(recurso(2, List.of(
                pod("taller-0", ElfoOperator.PodElfo.ESTADO_RUNNING),
                pod("taller-1", ElfoOperator.PodElfo.ESTADO_RUNNING))));
        assertFalse(r.cambiosAplicados());
        assertTrue(r.acciones().isEmpty());
    }

    @Test
    void testEstaConvergidoDetectaPodsCaidos() {
        ElfoOperator operador = new ElfoOperator();
        assertTrue(operador.estaConvergido(recurso(1,
                List.of(pod("taller-0", ElfoOperator.PodElfo.ESTADO_RUNNING)))));
        assertFalse(operador.estaConvergido(recurso(1,
                List.of(pod("taller-0", ElfoOperator.PodElfo.ESTADO_CRASHED)))));
        assertFalse(operador.estaConvergido(recurso(2,
                List.of(pod("taller-0", ElfoOperator.PodElfo.ESTADO_RUNNING)))));
    }

    @Test
    void testEstaConvergidoConRecursoNuloEsFalso() {
        assertFalse(new ElfoOperator().estaConvergido(null));
    }

    @Test
    void testEscalaDesdeCero() {
        ElfoOperator operador = new ElfoOperator();
        ElfoOperator.Reconciliacion r = operador.reconciliar(recurso(2, List.of()));
        assertEquals(2, r.estadoResultante().pods().size());
        assertEquals(2, r.acciones().size());
    }

    @Test
    void testEncogerHastaCero() {
        ElfoOperator operador = new ElfoOperator();
        ElfoOperator.Reconciliacion r = operador.reconciliar(recurso(0, List.of(
                pod("taller-0", ElfoOperator.PodElfo.ESTADO_RUNNING))));
        assertTrue(r.estadoResultante().pods().isEmpty());
    }

    @Test
    void testReconciliarConNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new ElfoOperator().reconciliar(null));
    }

    @Test
    void testSpecValidaCampos() {
        assertThrows(IllegalArgumentException.class,
                () -> new ElfoOperator.ElfoSpec("", 1, "v1"));
        assertThrows(IllegalArgumentException.class,
                () -> new ElfoOperator.ElfoSpec("taller", -1, "v1"));
        assertEquals("v1", new ElfoOperator.ElfoSpec("taller", 1, null).version());
    }

    @Test
    void testPodValidaCampos() {
        assertThrows(IllegalArgumentException.class,
                () -> new ElfoOperator.PodElfo("", ElfoOperator.PodElfo.ESTADO_RUNNING, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new ElfoOperator.PodElfo("p", "", 0));
        assertThrows(IllegalArgumentException.class,
                () -> new ElfoOperator.PodElfo("p", ElfoOperator.PodElfo.ESTADO_RUNNING, -1));
    }

    @Test
    void testRecursoInmutableAnteListaNula() {
        ElfoOperator.ElfoRecurso recurso = new ElfoOperator.ElfoRecurso("taller", spec(1), null);
        assertTrue(recurso.pods().isEmpty());
        assertThrows(UnsupportedOperationException.class,
                () -> recurso.pods().add(pod("x", ElfoOperator.PodElfo.ESTADO_RUNNING)));
    }

    @Test
    void testContratoReconcilerEsImplementable() {
        ElfoOperator.Reconciler<ElfoOperator.ElfoRecurso> reconciler =
                r -> new ElfoOperator.Reconciliacion(false, List.of(), r);
        ElfoOperator.ElfoRecurso recurso = recurso(1,
                List.of(pod("taller-0", ElfoOperator.PodElfo.ESTADO_RUNNING)));
        assertFalse(reconciler.reconciliar(recurso).cambiosAplicados());
    }

    @Test
    void testPodsSanosCuentaCorrectamente() {
        ElfoOperator.ElfoRecurso recurso = recurso(3, List.of(
                pod("taller-0", ElfoOperator.PodElfo.ESTADO_RUNNING),
                pod("taller-1", ElfoOperator.PodElfo.ESTADO_CRASHED),
                pod("taller-2", ElfoOperator.PodElfo.ESTADO_PENDIENTE)));
        assertEquals(1, recurso.podsSanos());
    }

    @Test
    void testBucleDeReconciliacionConvergeTrasVariasPasadas() {
        ElfoOperator operador = new ElfoOperator();
        ElfoOperator.ElfoRecurso estado = recurso(3, List.of(
                pod("taller-0", ElfoOperator.PodElfo.ESTADO_CRASHED)));
        int pasadas = 0;
        while (!operador.estaConvergido(estado) && pasadas < 10) {
            estado = operador.reconciliar(estado).estadoResultante();
            pasadas++;
        }
        assertTrue(operador.estaConvergido(estado));
        assertEquals(3, estado.pods().size());
        assertEquals(3, estado.podsSanos());
        assertTrue(pasadas <= 3);
    }
}
