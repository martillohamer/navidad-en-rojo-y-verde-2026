# Día 21 - El monitor de tormentas

## 🎄 La historia
El observatorio meteorológico del Polo Norte vigila la ruta de Nochebuena desde una red de estaciones repartidas por sectores. Cada estación manda una **medición**: el sector, la velocidad del viento y la visibilidad en kilómetros. El jefe del observatorio tiene tres pantallas encendidas: una con los sectores **en riesgo**, otra con las mediciones ordenadas de **mayor a menor peligrosidad** y una luz roja que se enciende cuando alguna estación registra **viento huracanado**. Sin estos tres informes, Santa volaría a ciegas. Tu misión es construir el `AlertaTormenta`.

## 🧠 Piensa antes de programar
- Un sector está en riesgo si el viento **supera** el umbral **o** si la visibilidad es crítica. ¿Qué operador lógico une esas dos condiciones? ¿Cuál es el umbral de riesgo y cuál el de visibilidad crítica?
- Para ordenar de más a menos peligroso, necesitas un único número que resuma "mucho viento y poca visibilidad". ¿Puedes combinar ambos valores en un método `peligrosidad()`?
- ¿Qué significa viento **huracanado**? Aquí lo fijamos en `118 km/h` o más. ¿La comparación es `>` o `>=`?
- Si dos sectores empatan a peligrosidad, ¿importa el orden? ¿Qué hace `sorted` con los empates?
- ¿Qué debe devolver cada método si la lista llega `null`? ¿Y si llega vacía?

## ✍️ Pseudocódigo guiado
1. El constructor guarda el `umbralRiesgo`. Si es negativo, lanza `IllegalArgumentException`.
2. **Sectores en riesgo**: `filter(m -> m.velocidadViento() > umbralRiesgo || m.visibilidadKm() < 1.0)` y `collect(Collectors.toList())`.
3. **Ordenar por peligrosidad**: define `peligrosidad = velocidadViento + (100 - visibilidadKm)` y usa `sorted(Comparator.comparingDouble(Medicion::peligrosidad).reversed())`.
4. **Alerta roja global**: `anyMatch(m -> m.velocidadViento() >= 118.0)`.
5. Si la lista es `null`, devuelve `List.of()` o `false` según el método.

## 💡 Pista oculta
<details>
<summary>Haz clic para ver la pista</summary>
Un `record Medicion(String sector, double velocidadViento, double visibilidadKm)` puede llevar métodos extra, como `peligrosidad()`. Eso te deja el comparador limpio: `Comparator.comparingDouble(Medicion::peligrosidad).reversed()`.
`anyMatch` **corta en cuanto encuentra** una coincidencia: en cuanto aparece un viento huracanado devuelve `true` sin recorrer el resto. Es la operación ideal para "existe al menos uno".
Cuidado con los límites: en el riesgo usamos `>` (estrictamente mayor) y en la alerta roja `>=` (huracanado incluye los 118 exactos). Los tests comprueban ambos comportamientos.
`Collectors.toList()` y `.toList()` (Java 16+) son equivalentes para volver a una lista; elige uno y sé consistente.
Si quieres ordenar sin crear un método auxiliar, también puedes usar `Comparator.comparingDouble((Medicion m) -> m.velocidadViento() + (100 - m.visibilidadKm()))`, pero el método `peligrosidad()` hace la intención mucho más legible.
</details>

## 📂 Dónde programar
Abre `src/main/java/reto/dia21/AlertaTormenta.java` y escribe ahí tu solución. El test está en `src/test/java/reto/dia21/Dia21Test.java`: léelo para ver qué se espera exactamente (no hace falta modificarlo).

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia21Test
```
Verás 8 tests en rojo 🔴. Tu misión: ponerlos en verde 🟢.

## 🚀 Reto extra opcional
El observatorio no solo quiere saber qué pasa ahora, sino **predecir** hacia dónde va la tormenta. Añade un método que reciba varias mediciones del **mismo sector** a lo largo del tiempo (una serie temporal ordenada) y estime la trayectoria: ¿sube o baja la velocidad del viento entre mediciones consecutivas? Un enfoque sencillo es calcular la diferencia media entre cada par de mediciones seguidas y clasificar la tendencia como "creciente", "decreciente" o "estable". ¿Qué haces si solo hay una medición? Escribe primero el test nuevo: primero el rojo, luego el verde.
