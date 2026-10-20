package reto.dia16;

public class MotorReglas {

    public String evaluarComportamiento(int cartasEnviadas, boolean seHaPortadoBien) {
        if (!seHaPortadoBien) {
            return "CARBON";
        }
        if (cartasEnviadas >= 1) {
            return "REGALO";
        }
        return "ESPERA";
    }
}