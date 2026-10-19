package reto.dia07;

public class Carta {
    private final String nino;
    private final int urgencia; // 1 (baja) a 5 (alta)

    public Carta(String nino, int urgencia) {
        this.nino = nino;
        this.urgencia = urgencia;
    }

    public String getNino() { return nino; }
    public int getUrgencia() { return urgencia; }
}