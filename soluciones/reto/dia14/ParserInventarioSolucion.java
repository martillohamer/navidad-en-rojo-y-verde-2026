package reto.dia14;

public class ParserInventario {

    public int extraerStockTotal(String lineaCsv) {
        if (lineaCsv == null || lineaCsv.isEmpty()) return 0;
        String[] partes = lineaCsv.split(",");
        if (partes.length >= 2) {
            return Integer.parseInt(partes[1].trim());
        }
        return 0;
    }
}