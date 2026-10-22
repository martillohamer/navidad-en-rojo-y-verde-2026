package reto.dia06;

public class TrineoSanta {

    private static final double UMBRAL_PESADO_KG = 5.0;

    public String determinarCategoriaYPrioridad(double pesoKg, boolean esFragil) {
        if (!Double.isFinite(pesoKg) || pesoKg < 0) {
            throw new IllegalArgumentException("Peso invalido: " + pesoKg);
        }
        if (pesoKg < UMBRAL_PESADO_KG) {
            return esFragil ? "LIVIANO-ALTA" : "LIVIANO-NORMAL";
        }
        return esFragil ? "PESADO-URGENTE" : "PESADO-NORMAL";
    }
}