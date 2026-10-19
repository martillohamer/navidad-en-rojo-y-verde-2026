package reto.dia15;

public class TrineoMagicoSolucion implements Trineo {
    private final int potenciaBase;

    public TrineoMagicoSolucion(int potenciaBase) {
        this.potenciaBase = potenciaBase;
    }

    @Override
    public int calcularVelocidadMax() {
        return this.potenciaBase * 2;
    }
}