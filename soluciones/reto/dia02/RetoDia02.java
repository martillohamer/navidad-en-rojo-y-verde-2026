package reto.dia02;

public class RetoDia02 {

    public String ropaSegunTemperatura(int grados) {
        if (grados < 15) {
            return "abrigo";
        } else if (grados >= 25) {
            return "camiseta";
        } else {
            return "jersey";
        }
    }
}
