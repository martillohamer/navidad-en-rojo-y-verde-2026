package reto.dia02;

public class RetoDia02 {

    private static final int UMBRAL_ABRIGO = 15;

    public String ropaSegunTemperatura(int grados) {
        return grados < UMBRAL_ABRIGO ? "abrigo" : "camiseta";
    }
}