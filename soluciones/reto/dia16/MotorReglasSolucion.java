package reto.dia16;

public class MotorReglasSolucion {
    public String evaluarComportamiento(int cartasEnviadas, boolean seHaPortadoBien) {
        if (seHaPortadoBien && cartasEnviadas > 0) {
            return "REGALO";
        }
        return "CARBON";
    }
}