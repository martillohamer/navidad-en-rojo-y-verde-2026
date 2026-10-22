# Día 20 - Control de calidad de juguetes

## 🎄 La historia
El Polo Norte tiene un departamento que casi nadie conoce: la **inspección de calidad**. Cada juguete que sale del taller pasa por una revisión que anota su categoría, un **nivel de defecto** de 0 (perfecto) a 10 (chatarra) y si quedó **aprobado** o no. El jefe de inspección, un elfo con gafas y muy poca paciencia, quiere tres informes diarios: qué juguetes aprobados están dentro del umbral permitido, cuántos juguetes hay de cada categoría y si se ha colado alguna **anomalía** (un juguete aprobado que, por su nivel de defecto, nunca debería haber pasado). Tu misión es construir el `ControlCalidad` que responde a esas tres preguntas usando **streams**.

## 🧠 Piensa antes de programar
- Un juguete está bien si está **aprobado y** su nivel de defecto no supera el umbral. ¿Cómo encadenas esas dos condiciones sin escribir dos bucles?
- Para contar por categoría, ¿prefieres un `for` con un `Map` y `merge`, o un `Collectors.groupingBy(...)` con `Collectors.counting()`? ¿Qué devuelve este último, `Integer` o `Long`?
- ¿Qué es una anomalía? Fíjate: un juguete aprobado con defecto **alto** es contradictorio. ¿La condición se compara con `>=` o con `>` respecto al umbral crítico?
- ¿Qué debe hacer cada método si la lista llega `null`? ¿Y si llega vacía? Piensa qué estructura vacía es natural devolver en cada caso (`List.of()` frente a `Map.of()`).
- En un stream, `filter` no modifica la lista original: devuelve un nuevo stream. ¿Qué operación final (`collect`) necesitas para volver a tener una `List` o un `Map`?

## ✍️ Pseudocódigo guiado
1. Si la lista de juguetes es `null` (o vacía), cada método devuelve su estructura vacía: `List.of()` o `Map.of()`.
2. **Filtrar aprobados**: parte de la lista como stream, aplica `filter(Juguete::aprobado)` y luego `filter` con `nivelDefecto <= umbralDefecto`; cierra con `collect(Collectors.toList())`.
3. **Contar por categoría**: `collect(Collectors.groupingBy(Juguete::categoria, Collectors.counting()))`.
4. **Detectar anomalías**: `filter(Juguete::aprobado)` y luego `filter` con `nivelDefecto >= defectoCritico`; cierra con `toList()`.
5. Devuelve el resultado de cada operación.

## 💡 Pista oculta
<details>
<summary>Haz clic para ver la pista</summary>
Un `record Juguete(String nombre, String categoria, int nivelDefecto, boolean aprobado)` genera los accesores `nombre()`, `categoria()`, `nivelDefecto()` y `aprobado()`. Encadenar `filter` con referencias a métodos (`.filter(Juguete::aprobado)`) deja el código más limpio que con lambdas largas.
`Collectors.counting()` devuelve `Long`, por eso el `Map` resultante es `Map<String, Long>` y en los tests comparas con `2L`, no con `2`.
`List.of()` y `Map.of()` son estructuras **inmutables** y vacías: perfectas para el caso `null`, sin crear basura ni arriesgar `NullPointerException`.
Verás que `.collect(Collectors.toList())` también se puede escribir con `.toList()` (Java 16+); ambas crean una lista con los elementos del stream.
En los tests, encadenar `assertTrue(resultado.stream().allMatch(...))` comprueba propiedades de **todo** el resultado sin listar cada elemento.
</details>

## 📂 Dónde programar
Abre `src/main/java/reto/dia20/ControlCalidad.java` y escribe ahí tu solución. El test está en `src/test/java/reto/dia20/Dia20Test.java`: léelo para ver qué se espera exactamente (no hace falta modificarlo).

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia20Test
```
Verás 5 tests en rojo 🔴. Tu misión: ponerlos en verde 🟢.

## 🚀 Reto extra opcional
La nave procesa **millones** de juguetes y el informe secuencial tarda demasiado. Investiga el **procesamiento paralelo** en streams: una línea de base `.parallelStream()` sobre la misma lista no cambia el resultado, pero reparte el trabajo entre los núcleos de la CPU. ¿Se te ocurre qué partes de tu código son seguras de paralelizar y cuáles no? Añade un test que confirme que el resultado con `parallelStream` coincide exactamente con el secuencial: primero el rojo, luego el verde.
