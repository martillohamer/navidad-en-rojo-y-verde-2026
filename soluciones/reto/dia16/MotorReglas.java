package reto.dia16;

public class MotorReglas {

    public String evaluarComportamiento(int cartasEnviadas, boolean seHaPortadoBien) {
        if (seHaPortadoBien && cartasEnviadas > 0) {
            return "REGALO";
        }
        return "CARBON";
    }
}