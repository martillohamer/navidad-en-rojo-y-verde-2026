package reto.dia03;

public class RetoDia03 {

    public String cuentaAtras(int dias) {
        StringBuilder sb = new StringBuilder();
        for (int i = dias; i >= 1; i--) {
            sb.append(i).append(", ");
        }
        sb.append("\u00A1Navidad!");
        return sb.toString();
    }
}