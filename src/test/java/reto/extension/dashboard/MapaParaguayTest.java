package reto.extension.dashboard;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapaParaguayTest {

    private static final String RECURSO_GEOJSON = "/static/data/paraguay-departamentos.geojson";
    private static final String RECURSO_HTML = "/static/paraguay.html";
    private static final double RADIO_TIERRA_KM = 6371.0088;
    private static final double AREA_OFICIAL_KM2 = 406752.0;
    private static final Set<String> NOMBRES = Set.of(
            "Asunción", "Concepción", "San Pedro", "Cordillera", "Guairá", "Caaguazú",
            "Caazapá", "Itapúa", "Misiones", "Paraguarí", "Alto Paraná", "Central",
            "Ñeembucú", "Amambay", "Canindeyú", "Presidente Hayes", "Boquerón", "Alto Paraguay");

    private static String geojson;
    private static String html;
    private static List<Map<String, Object>> features;

    @BeforeAll
    @SuppressWarnings("unchecked")
    static void cargar() throws IOException {
        geojson = leer(RECURSO_GEOJSON);
        html = leer(RECURSO_HTML);
        Map<String, Object> raiz = (Map<String, Object>) new Json(geojson).parse();
        features = (List<Map<String, Object>>) raiz.get("features");
    }

    private static String leer(String recurso) throws IOException {
        try (InputStream in = MapaParaguayTest.class.getResourceAsStream(recurso)) {
            assertNotNull(in, "No se encontro el recurso " + recurso);
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void testGeojsonTieneLasDieciochoEntidadesExactas() {
        assertEquals(18, features.size());
        Set<String> nombres = new HashSet<>();
        Set<String> ids = new HashSet<>();
        for (Map<String, Object> f : features) {
            Map<String, Object> props = props(f);
            nombres.add((String) props.get("nombre"));
            ids.add((String) props.get("id"));
        }
        assertEquals(NOMBRES, nombres);
        assertEquals(18, ids.size());
    }

    @Test
    void testCadaEntidadTieneLasPropiedadesNormalizadas() {
        for (Map<String, Object> f : features) {
            Map<String, Object> props = props(f);
            String nombre = (String) props.get("nombre");
            assertEquals(Set.of("id", "nombre", "romano", "capital", "capitalLat", "capitalLon", "poblacion"),
                    props.keySet(), "Propiedades inesperadas en " + nombre);
            assertFalse(((String) props.get("id")).isBlank());
            assertFalse(((String) props.get("romano")).isBlank());
            assertFalse(((String) props.get("capital")).isBlank());
            assertTrue(((Number) props.get("poblacion")).doubleValue() > 0, "Poblacion invalida en " + nombre);
            double lat = ((Number) props.get("capitalLat")).doubleValue();
            double lon = ((Number) props.get("capitalLon")).doubleValue();
            assertTrue(lat < -19 && lat > -28, "Latitud fuera de Paraguay en " + nombre);
            assertTrue(lon < -54 && lon > -63, "Longitud fuera de Paraguay en " + nombre);
        }
    }

    @Test
    void testBoundingBoxDentroDeLosLimitesDeParaguay() {
        double minLat = 90;
        double maxLat = -90;
        double minLon = 180;
        double maxLon = -180;
        for (Map<String, Object> f : features) {
            for (List<List<List<Double>>> poligono : poligonos(f)) {
                for (List<List<Double>> anillo : poligono) {
                    for (List<Double> p : anillo) {
                        minLat = Math.min(minLat, p.get(1));
                        maxLat = Math.max(maxLat, p.get(1));
                        minLon = Math.min(minLon, p.get(0));
                        maxLon = Math.max(maxLon, p.get(0));
                    }
                }
            }
        }
        assertTrue(minLat >= -27.7 && maxLat <= -19.2, "Latitud fuera de rango: " + minLat + ".." + maxLat);
        assertTrue(minLon >= -62.7 && maxLon <= -54.2, "Longitud fuera de rango: " + minLon + ".." + maxLon);
        assertTrue(minLat < -27.5 && maxLat > -19.4, "La silueta no cubre el pais completo");
        assertTrue(minLon < -62.5 && maxLon > -54.4, "La silueta no cubre el pais completo");
    }

    @Test
    void testCadaCapitalCaeDentroDeSuPropioPoligono() {
        for (Map<String, Object> f : features) {
            Map<String, Object> props = props(f);
            String nombre = (String) props.get("nombre");
            double lat = ((Number) props.get("capitalLat")).doubleValue();
            double lon = ((Number) props.get("capitalLon")).doubleValue();
            assertTrue(contiene(f, lon, lat), "La capital de " + nombre + " queda fuera del departamento");
        }
    }

    @Test
    void testOrdenEspacialDeLosTerritorios() {
        Map<String, double[]> c = new HashMap<>();
        for (Map<String, Object> f : features) {
            Map<String, Object> p = props(f);
            c.put((String) p.get("nombre"), new double[]{
                    ((Number) p.get("capitalLat")).doubleValue(),
                    ((Number) p.get("capitalLon")).doubleValue()});
        }
        assertTrue(c.get("Alto Paraguay")[0] > c.get("Boquerón")[0], "Alto Paraguay debe estar al norte de Boqueron");
        assertTrue(c.get("Boquerón")[1] < c.get("Concepción")[1], "Boqueron debe estar al oeste de Concepcion");
        assertTrue(c.get("Itapúa")[0] < c.get("Asunción")[0], "Itapua debe estar al sur de Asuncion");
        assertTrue(c.get("Itapúa")[1] > c.get("Asunción")[1], "Itapua debe estar al este de Asuncion");
        assertTrue(c.get("Ñeembucú")[0] < c.get("Misiones")[0], "Neembucu debe estar al sur de Misiones");
        assertTrue(c.get("Ñeembucú")[1] < c.get("Misiones")[1], "Neembucu debe estar al oeste de Misiones");
    }

    @Test
    void testAreaTotalSeAproximaALaSuperficieOficial() {
        double total = 0;
        for (Map<String, Object> f : features) {
            total += areaGeometria(f);
        }
        double desvio = Math.abs(total - AREA_OFICIAL_KM2) / AREA_OFICIAL_KM2;
        assertTrue(desvio < 0.03, "Area total " + Math.round(total) + " km2 se desvia " + Math.round(desvio * 100) + "%");
        assertTrue(total > 380000 && total < 430000, "Area total implausible: " + total);
    }

    @Test
    void testGeojsonEsLigeroParaServirsePorHttp() {
        assertTrue(geojson.getBytes(StandardCharsets.UTF_8).length < 300 * 1024,
                "El GeoJSON supera los 300 KB");
    }

    @Test
    void testHtmlUsaElGeojsonRealYLasLibreriasLocales() {
        assertTrue(html.contains("paraguay-departamentos.geojson"), "No carga el GeoJSON real");
        assertTrue(html.contains("type=\"importmap\""), "Falta el importmap para las librerias locales");
        assertTrue(html.contains("./vendor/three.module.js"), "Three.js no se sirve localmente");
        assertTrue(html.contains("./vendor/d3-geo.esm.js"), "d3-geo no se sirve localmente");
        assertTrue(html.contains("./vendor/polylabel.esm.js"), "polylabel no se sirve localmente");
        assertFalse(html.contains("cdn.jsdelivr"), "No debe depender de CDN en tiempo de ejecucion");
        assertFalse(html.contains("unpkg.com"), "No debe depender de CDN en tiempo de ejecucion");
        assertFalse(html.contains("three.min.js"), "No debe usar el bundle clasico de Three.js");
    }

    @Test
    void testHtmlProyectaConUnaSolaFuenteDeVerdad() {
        assertTrue(html.contains("geoMercator"), "La proyeccion debe ser d3.geoMercator");
        assertTrue(html.contains("fitSize"), "La proyeccion debe ajustarse con fitSize");
        assertTrue(html.contains("function proyectar"), "Falta la funcion proyectar");
        assertTrue(html.contains("function invertir"), "Falta la funcion invertir");
        assertTrue(html.contains("invertir("), "La telemetria debe usar coordenadas invertidas de la proyeccion");
        assertTrue(html.contains("geoContains"), "La deteccion del departamento activo debe usar geoContains");
        assertTrue(html.contains("polylabel"), "Las etiquetas deben usar polylabel");
    }

    @Test
    void testHtmlConservaLaIdentidadYLaRutaDelTrineo() {
        assertTrue(html.contains("RADAR TÁCTICO DE PARAGUAY"));
        assertTrue(html.contains("18 entidades"));
        assertTrue(html.contains("Asunción"));
        assertTrue(html.contains("Boquerón"));
        assertTrue(html.contains("CatmullRomCurve3"));
        assertTrue(html.contains("position.set(0, 150, 110)"));
        assertTrue(html.contains("Restablecer vista"));
        assertFalse(html.contains("RADAR 3D DEL POLO NORTE"));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> props(Map<String, Object> f) {
        return (Map<String, Object>) f.get("properties");
    }

    @SuppressWarnings("unchecked")
    private static List<List<List<List<Double>>>> poligonos(Map<String, Object> f) {
        Map<String, Object> geom = (Map<String, Object>) f.get("geometry");
        List<Object> coords = (List<Object>) geom.get("coordinates");
        List<List<List<List<Double>>>> out = new ArrayList<>();
        if ("Polygon".equals(geom.get("type"))) {
            out.add((List<List<List<Double>>>) (List<?>) coords);
        } else {
            for (Object p : coords) {
                out.add((List<List<List<Double>>>) (List<?>) p);
            }
        }
        return out;
    }

    private static boolean contiene(Map<String, Object> f, double lon, double lat) {
        for (List<List<List<Double>>> poligono : poligonos(f)) {
            if (!enAnillo(poligono.get(0), lon, lat)) {
                continue;
            }
            boolean enHueco = false;
            for (int k = 1; k < poligono.size(); k++) {
                if (enAnillo(poligono.get(k), lon, lat)) {
                    enHueco = true;
                    break;
                }
            }
            if (!enHueco) {
                return true;
            }
        }
        return false;
    }

    private static boolean enAnillo(List<List<Double>> anillo, double x, double y) {
        boolean dentro = false;
        for (int i = 0, j = anillo.size() - 1; i < anillo.size(); j = i++) {
            double xi = anillo.get(i).get(0);
            double yi = anillo.get(i).get(1);
            double xj = anillo.get(j).get(0);
            double yj = anillo.get(j).get(1);
            if ((yi > y) != (yj > y) && x < (xj - xi) * (y - yi) / (yj - yi) + xi) {
                dentro = !dentro;
            }
        }
        return dentro;
    }

    private static double areaGeometria(Map<String, Object> f) {
        double total = 0;
        for (List<List<List<Double>>> poligono : poligonos(f)) {
            total += areaAnillo(poligono.get(0));
            for (int k = 1; k < poligono.size(); k++) {
                total -= areaAnillo(poligono.get(k));
            }
        }
        return total;
    }

    private static double areaAnillo(List<List<Double>> anillo) {
        double suma = 0;
        for (int i = 0; i < anillo.size(); i++) {
            double lon1 = Math.toRadians(anillo.get(i).get(0));
            double lat1 = Math.toRadians(anillo.get(i).get(1));
            double lon2 = Math.toRadians(anillo.get((i + 1) % anillo.size()).get(0));
            double lat2 = Math.toRadians(anillo.get((i + 1) % anillo.size()).get(1));
            suma += (lon2 - lon1) * (2 + Math.sin(lat1) + Math.sin(lat2));
        }
        return Math.abs(suma * RADIO_TIERRA_KM * RADIO_TIERRA_KM / 2);
    }

    private static final class Json {
        private final String s;
        private int i;

        Json(String s) {
            this.s = s;
        }

        Object parse() {
            Object valor = valor();
            saltar();
            if (i != s.length()) {
                throw new IllegalArgumentException("JSON con contenido sobrante en " + i);
            }
            return valor;
        }

        private Object valor() {
            saltar();
            char c = s.charAt(i);
            switch (c) {
                case '{':
                    return objeto();
                case '[':
                    return arreglo();
                case '"':
                    return cadena();
                case 't':
                    return literal("true", Boolean.TRUE);
                case 'f':
                    return literal("false", Boolean.FALSE);
                case 'n':
                    return literal("null", null);
                default:
                    return numero();
            }
        }

        private Map<String, Object> objeto() {
            Map<String, Object> out = new HashMap<>();
            i++;
            saltar();
            if (s.charAt(i) == '}') {
                i++;
                return out;
            }
            while (true) {
                saltar();
                String clave = cadena();
                saltar();
                if (s.charAt(i++) != ':') {
                    throw new IllegalArgumentException("Se esperaba ':' en " + i);
                }
                out.put(clave, valor());
                saltar();
                char c = s.charAt(i++);
                if (c == '}') {
                    return out;
                }
                if (c != ',') {
                    throw new IllegalArgumentException("Se esperaba ',' en " + i);
                }
            }
        }

        private List<Object> arreglo() {
            List<Object> out = new ArrayList<>();
            i++;
            saltar();
            if (s.charAt(i) == ']') {
                i++;
                return out;
            }
            while (true) {
                out.add(valor());
                saltar();
                char c = s.charAt(i++);
                if (c == ']') {
                    return out;
                }
                if (c != ',') {
                    throw new IllegalArgumentException("Se esperaba ',' en " + i);
                }
            }
        }

        private String cadena() {
            StringBuilder b = new StringBuilder();
            i++;
            while (true) {
                char c = s.charAt(i++);
                if (c == '"') {
                    return b.toString();
                }
                if (c != '\\') {
                    b.append(c);
                    continue;
                }
                char e = s.charAt(i++);
                switch (e) {
                    case '"':
                    case '\\':
                    case '/':
                        b.append(e);
                        break;
                    case 'b':
                        b.append('\b');
                        break;
                    case 'f':
                        b.append('\f');
                        break;
                    case 'n':
                        b.append('\n');
                        break;
                    case 'r':
                        b.append('\r');
                        break;
                    case 't':
                        b.append('\t');
                        break;
                    case 'u':
                        b.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
                        i += 4;
                        break;
                    default:
                        throw new IllegalArgumentException("Escape invalido en " + i);
                }
            }
        }

        private Object literal(String texto, Object valor) {
            if (!s.startsWith(texto, i)) {
                throw new IllegalArgumentException("Literal invalido en " + i);
            }
            i += texto.length();
            return valor;
        }

        private Double numero() {
            int inicio = i;
            while (i < s.length() && "+-0123456789.eE".indexOf(s.charAt(i)) >= 0) {
                i++;
            }
            return Double.valueOf(s.substring(inicio, i));
        }

        private void saltar() {
            while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
                i++;
            }
        }
    }
}
