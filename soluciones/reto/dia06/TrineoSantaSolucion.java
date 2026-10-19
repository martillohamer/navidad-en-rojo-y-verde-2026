package reto.dia06;

public class TrineoSanta {

    public String determinarCategoriaYPrioridad(double pesoKg, boolean esFragil) {
        if (pesoKg < 5.0) {
            return esFragil ? "LIVIANO-ALTA" : "LIVIANO-NORMAL";
        } else {
            return esFragil ? "PESADO-URGENTE" : "PESADO-NORMAL";
        }
    }
}