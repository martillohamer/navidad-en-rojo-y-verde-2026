package reto.dia14;

public class ParserInventario {

    // Linea con formato: nombre,stock,categoria
    public int extraerStockTotal(String lineaCsv) {
        String[] partes = lineaCsv.split(",");
        if (partes.length < 2) {
            throw new IllegalArgumentException("Linea CSV incompleta: " + lineaCsv);
        }
        return Integer.parseInt(partes[1].trim());
    }
}