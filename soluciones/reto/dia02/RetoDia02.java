package reto.dia02;

public class RetoDia02 {
    // Una condicion decide: menos de 15 grados -> abrigo, si no -> camiseta
    public String ropaSegunTemperatura(int grados) {
        if (grados < 15) {
            return "abrigo";
        }
        return "camiseta";
    }
}