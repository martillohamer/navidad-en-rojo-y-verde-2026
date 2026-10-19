package reto.dia03;

public class RetoDia03 {

    public String cuentaAtras(int dias) {
        if (dias <= 0) {
            return "¡Navidad!";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = dias; i >= 1; i--) {
            sb.append(i).append(", ");
        }
        sb.append("¡Navidad!");
        return sb.toString();
    }
}
