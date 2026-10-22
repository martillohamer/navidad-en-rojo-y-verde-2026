package reto.extension.ledger;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MerkleLedgerTest {

    private MerkleLedger.Transaccion tx(String id, String destino, double peso) {
        return new MerkleLedger.Transaccion(id, destino, peso);
    }

    @Test
    void testHashSha256TieneLaLongitudCorrecta() {
        String h = MerkleLedger.hash("navidad");
        assertEquals(64, h.length());
    }

    @Test
    void testHashEsDeterminista() {
        assertEquals(MerkleLedger.hash("polo norte"), MerkleLedger.hash("polo norte"));
    }

    @Test
    void testHashDeTextosDistintosEsDistinto() {
        assertNotEquals(MerkleLedger.hash("regalo"), MerkleLedger.hash("carbon"));
    }

    @Test
    void testMerkleRootDeListaVaciaEsElHashVacio() {
        assertEquals(MerkleLedger.hash("vacio"), MerkleLedger.calcularMerkleRoot(List.of()));
        assertEquals(MerkleLedger.hash("vacio"), MerkleLedger.calcularMerkleRoot(null));
    }

    @Test
    void testMerkleRootDeUnaTransaccionEsSuHash() {
        MerkleLedger.Transaccion t = tx("r1", "Ana", 3.5);
        assertEquals(MerkleLedger.hashTransaccion(t),
                MerkleLedger.calcularMerkleRoot(List.of(t)));
    }

    @Test
    void testMerkleRootDeDosTransaccionesCombinaAmbas() {
        MerkleLedger.Transaccion a = tx("r1", "Ana", 3.5);
        MerkleLedger.Transaccion b = tx("r2", "Luis", 1.0);
        String esperado = MerkleLedger.hash(
                MerkleLedger.hashTransaccion(a) + MerkleLedger.hashTransaccion(b));
        assertEquals(esperado, MerkleLedger.calcularMerkleRoot(List.of(a, b)));
    }

    @Test
    void testMerkleRootConNumeroImparDeTransaccionesEsEstable() {
        MerkleLedger.Transaccion a = tx("r1", "Ana", 3.5);
        MerkleLedger.Transaccion b = tx("r2", "Luis", 1.0);
        MerkleLedger.Transaccion c = tx("r3", "Eva", 2.0);
        String raiz = MerkleLedger.calcularMerkleRoot(List.of(a, b, c));
        assertEquals(64, raiz.length());
        assertEquals(raiz, MerkleLedger.calcularMerkleRoot(List.of(a, b, c)));
    }

    @Test
    void testCambiarUnaTransaccionCambiaLaRaiz() {
        List<MerkleLedger.Transaccion> originales = List.of(tx("r1", "Ana", 3.5), tx("r2", "Luis", 1.0));
        String raizOriginal = MerkleLedger.calcularMerkleRoot(originales);
        List<MerkleLedger.Transaccion> manipuladas = List.of(tx("r1", "Ana", 3.5), tx("r2", "Luis", 99.0));
        assertNotEquals(raizOriginal, MerkleLedger.calcularMerkleRoot(manipuladas));
    }

    @Test
    void testVerificarCoincideYDetectaManipulacion() {
        List<MerkleLedger.Transaccion> txs = List.of(tx("r1", "Ana", 3.5), tx("r2", "Luis", 1.0));
        String raiz = MerkleLedger.calcularMerkleRoot(txs);
        assertTrue(MerkleLedger.verificar(txs, raiz));
        assertFalse(MerkleLedger.verificar(txs, "raiz-falsa"));
    }
}
