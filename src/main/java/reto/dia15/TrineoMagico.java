package reto.dia15;

public class TrineoMagico implements Trineo {
    private final int potenciaBase;

    public TrineoMagico(int potenciaBase) {
        this.potenciaBase = potenciaBase;
    }

    @Override
    public int calcularVelocidadMax() {
        return potenciaBase * 2; // la magia duplica la potencia
    }
}