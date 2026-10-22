package reto.extension.ledger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;

public class MerkleLedger {

    // Una transaccion de regalo registrada en el ledger inmutable.
    public record Transaccion(String idRegalo, String destino, double peso) {
    }

    // Hash SHA-256 de un texto, devuelto en hexadecimal.
    public static String hash(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    // Hash de una transaccion combinando sus campos.
    public static String hashTransaccion(Transaccion t) {
        if (t == null) {
            return hash("vacio");
        }
        return hash(t.idRegalo() + "|" + t.destino() + "|" + t.peso());
    }

    // Calcula la raiz de Merkle de una lista de transacciones.
    public static String calcularMerkleRoot(List<Transaccion> transacciones) {
        if (transacciones == null || transacciones.isEmpty()) {
            return hash("vacio");
        }

        List<String> nivel = new ArrayList<>();
        for (Transaccion t : transacciones) {
            nivel.add(hashTransaccion(t));
        }

        while (nivel.size() > 1) {
            List<String> siguiente = new ArrayList<>();
            for (int i = 0; i < nivel.size(); i += 2) {
                String izquierda = nivel.get(i);
                String derecha = (i + 1 < nivel.size()) ? nivel.get(i + 1) : izquierda;
                siguiente.add(hash(izquierda + derecha));
            }
            nivel = siguiente;
        }
        return nivel.get(0);
    }

    // Verifica que un hash raiz dado coincide con el calculado.
    public static boolean verificar(List<Transaccion> transacciones, String raizEsperada) {
        return calcularMerkleRoot(transacciones).equals(raizEsperada);
    }
}
