package reto.dia03;

public class RetoDia03 {
    // Un bucle for que baja de "dias" a 1 y al final anade el mensaje
    public String cuentaAtras(int dias) {
        StringBuilder sb = new StringBuilder();
        for (int i = dias; i >= 1; i--) {
            sb.append(i).append(", ");
        }
        sb.append("\u00A1Navidad!");
        return sb.toString();
    }
}