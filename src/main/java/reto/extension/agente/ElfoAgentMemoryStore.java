package reto.extension.agente;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ElfoAgentMemoryStore {

    // Una memoria del agente elfo: la carta de un nino y su representacion vectorial.
    public record CartaMemoria(String idNino, String contenidoCarta,
            double[] embeddingVectorial, double scoreAptitud) {
    }

    private final List<CartaMemoria> memorias = new ArrayList<>();

    // Guarda una carta en la memoria de largo plazo del agente.
    public void almacenar(CartaMemoria memoria) {
        if (memoria != null) {
            memorias.add(memoria);
        }
    }

    // Numero de memorias almacenadas.
    public int tamano() {
        return memorias.size();
    }

    // Similitud de coseno entre dos vectores: producto punto / producto de normas.
    public static double similitudCoseno(double[] a, double[] b) {
        if (a == null || b == null || a.length != b.length || a.length == 0) {
            return 0.0;
        }
        double producto = 0.0;
        double normaA = 0.0;
        double normaB = 0.0;
        for (int i = 0; i < a.length; i++) {
            producto += a[i] * b[i];
            normaA += a[i] * a[i];
            normaB += b[i] * b[i];
        }
        if (normaA == 0.0 || normaB == 0.0) {
            return 0.0;
        }
        return producto / (Math.sqrt(normaA) * Math.sqrt(normaB));
    }

    // Recupera la memoria mas parecida al vector consultado usando streams.
    public Optional<CartaMemoria> buscarMasSimilar(double[] consulta) {
        if (consulta == null || memorias.isEmpty()) {
            return Optional.empty();
        }
        return memorias.stream()
                .max(Comparator.comparingDouble(m -> similitudCoseno(consulta, m.embeddingVectorial())));
    }

    // Recupera las k memorias mas parecidas ordenadas de mayor a menor similitud.
    public List<CartaMemoria> buscarTopK(double[] consulta, int k) {
        if (consulta == null || k <= 0) {
            return List.of();
        }
        return memorias.stream()
                .sorted(Comparator.comparingDouble(
                        (CartaMemoria m) -> similitudCoseno(consulta, m.embeddingVectorial())).reversed())
                .limit(k)
                .toList();
    }

    // Devuelve las memorias que superan un umbral minimo de aptitud.
    public List<CartaMemoria> memoriasAptas(double umbral) {
        return memorias.stream()
                .filter(m -> m.scoreAptitud() >= umbral)
                .toList();
    }
}
