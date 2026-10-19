package reto.dia15;

public class TrineoMagico implements Trineo {
    private final int potenciaBase;

    public TrineoMagico(int potenciaBase) {
        this.potenciaBase = potenciaBase;
    }

    @Override
    public int calcularVelocidadMax() {
        return this.potenciaBase * 2;
    }
}