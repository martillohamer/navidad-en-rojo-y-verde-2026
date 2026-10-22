package reto.extension.agente;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ElfoAgentMemoryTest {

    private ElfoAgentMemoryStore nuevoStore() {
        return new ElfoAgentMemoryStore();
    }

    private ElfoAgentMemoryStore.CartaMemoria carta(String id, double[] vector, double aptitud) {
        return new ElfoAgentMemoryStore.CartaMemoria(id, "Querido Papa Noel...", vector, aptitud);
    }

    @Test
    void testSimilitudCosenoDeVectoresIdenticosEsUno() {
        double sim = ElfoAgentMemoryStore.similitudCoseno(
                new double[] {1.0, 0.0, 0.0}, new double[] {1.0, 0.0, 0.0});
        assertEquals(1.0, sim, 1e-9);
    }

    @Test
    void testSimilitudCosenoDeVectoresOrtogonalesEsCero() {
        double sim = ElfoAgentMemoryStore.similitudCoseno(
                new double[] {1.0, 0.0}, new double[] {0.0, 1.0});
        assertEquals(0.0, sim, 1e-9);
    }

    @Test
    void testSimilitudCosenoConVectorNuloDevuelveCero() {
        double sim = ElfoAgentMemoryStore.similitudCoseno(
                new double[] {0.0, 0.0}, new double[] {1.0, 1.0});
        assertEquals(0.0, sim, 1e-9);
    }

    @Test
    void testSimilitudCosenoConLongitudesDistintasDevuelveCero() {
        double sim = ElfoAgentMemoryStore.similitudCoseno(
                new double[] {1.0, 0.0}, new double[] {1.0, 0.0, 0.0});
        assertEquals(0.0, sim, 1e-9);
    }

    @Test
    void testAlmacenarIncrementaElTamano() {
        ElfoAgentMemoryStore store = nuevoStore();
        store.almacenar(carta("n1", new double[] {1.0, 0.0}, 0.5));
        store.almacenar(carta("n2", new double[] {0.0, 1.0}, 0.9));
        store.almacenar(null);
        assertEquals(2, store.tamano());
    }

    @Test
    void testBuscarMasSimilarEncuentraElVectorAlineado() {
        ElfoAgentMemoryStore store = nuevoStore();
        store.almacenar(carta("n1", new double[] {1.0, 0.0, 0.0}, 0.5));
        store.almacenar(carta("n2", new double[] {0.0, 1.0, 0.0}, 0.5));
        Optional<ElfoAgentMemoryStore.CartaMemoria> mejor =
                store.buscarMasSimilar(new double[] {0.9, 0.1, 0.0});
        assertTrue(mejor.isPresent());
        assertEquals("n1", mejor.get().idNino());
    }

    @Test
    void testBuscarMasSimilarConMemoriaVaciaEsVacio() {
        assertTrue(nuevoStore().buscarMasSimilar(new double[] {1.0, 0.0}).isEmpty());
    }

    @Test
    void testBuscarMasSimilarConConsultaNulaEsVacio() {
        ElfoAgentMemoryStore store = nuevoStore();
        store.almacenar(carta("n1", new double[] {1.0, 0.0}, 0.5));
        assertTrue(store.buscarMasSimilar(null).isEmpty());
    }

    @Test
    void testBuscarTopKDevuelveLosMasSimilaresEnOrden() {
        ElfoAgentMemoryStore store = nuevoStore();
        store.almacenar(carta("n1", new double[] {1.0, 0.0, 0.0}, 0.5));
        store.almacenar(carta("n2", new double[] {0.0, 1.0, 0.0}, 0.5));
        store.almacenar(carta("n3", new double[] {0.7, 0.7, 0.0}, 0.5));
        List<ElfoAgentMemoryStore.CartaMemoria> top =
                store.buscarTopK(new double[] {1.0, 0.0, 0.0}, 2);
        assertEquals(2, top.size());
        assertEquals("n1", top.get(0).idNino());
        assertEquals("n3", top.get(1).idNino());
    }

    @Test
    void testBuscarTopKConLimiteInvalidoDevuelveListaVacia() {
        ElfoAgentMemoryStore store = nuevoStore();
        store.almacenar(carta("n1", new double[] {1.0, 0.0}, 0.5));
        assertTrue(store.buscarTopK(new double[] {1.0, 0.0}, 0).isEmpty());
        assertTrue(store.buscarTopK(new double[] {1.0, 0.0}, -3).isEmpty());
    }

    @Test
    void testMemoriasAptasFiltraPorUmbral() {
        ElfoAgentMemoryStore store = nuevoStore();
        store.almacenar(carta("n1", new double[] {1.0}, 0.4));
        store.almacenar(carta("n2", new double[] {1.0}, 0.8));
        store.almacenar(carta("n3", new double[] {1.0}, 0.95));
        List<ElfoAgentMemoryStore.CartaMemoria> aptas = store.memoriasAptas(0.8);
        assertEquals(2, aptas.size());
    }

    @Test
    void testRecordCartaMemoriaExponeSusComponentes() {
        ElfoAgentMemoryStore.CartaMemoria c = carta("n42", new double[] {0.1, 0.2}, 0.7);
        assertEquals("n42", c.idNino());
        assertEquals("Querido Papa Noel...", c.contenidoCarta());
        assertEquals(0.7, c.scoreAptitud(), 1e-9);
        assertEquals(2, c.embeddingVectorial().length);
    }
}
