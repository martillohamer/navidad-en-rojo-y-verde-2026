# Día 12 - El gran informe de producción

## 🎄 La historia
La fábrica de juguetes del Polo Norte ha cerrado la jornada y Santa necesita un único número: **cuántos juguetes de calidad se han producido hoy**. Cada duende anota su producción en una lista, pero solo cuentan las cifras que superan un umbral de calidad (por ejemplo, 10). El resto se descartan. Con esa suma, Santa decide si la fábrica ha cumplido el objetivo de la noche. Hoy tu trabajo es encadenar operaciones como un jefe de sala: filtrar, transformar y totalizar.

## 🧠 Piensa antes de programar
- Con las producciones `[5, 20, 15, 30, 8]` y un umbral de `10`, ¿qué cifras se suman? ¿Y cuál es el total? (20, 15 y 30 → 65)
- Una producción **igual** al umbral (10) ¿cuenta o se queda fuera? Fíjate en la palabra "altas" del enunciado del método.
- ¿Qué devuelve la suma si la lista está vacía? ¿Y si todos los valores están por debajo del umbral?
- ¿Qué ventaja tiene encadenar un `stream` frente a un `for` con un `if` dentro? ¿En cuál de los dos se ve mejor la intención de cada paso?

## ✍️ Pseudocódigo guiado
1. Toma la lista de producciones.
2. **Filtra**: quédate solo con las que son estrictamente mayores que el umbral.
3. **Transforma**: conviértelas a número entero (`int`).
4. **Totaliza**: suma todas y devuelve la suma.

## 💡 Pista oculta
<details>
<summary>Haz clic para ver la pista</summary>
La receta cabe en una línea encadenada: `producciones.stream().filter(p -> p > umbral).mapToInt(Integer::intValue).sum()`.
El paso `mapToInt` es importante: si sumas directamente valores `Integer` con `sum()`, Java te avisa de que podrías saturar el rango de `int`; con `mapToInt` sumas ya en `int` y desaparecen los avisos.
El `filter` es el que aplica el umbral: `p > umbral` (estrictamente mayor).
</details>

## 📂 Dónde programar
Abre `src/main/java/reto/dia12/GeneradorInformes.java` y escribe ahí tu solución. El test está en `src/test/java/reto/dia12/Dia12Test.java`: léelo para ver qué se espera exactamente (no hace falta modificarlo).

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia12Test
```
Verás 1 test en rojo 🔴. Tu misión: ponerlo en verde 🟢.

## 🚀 Reto extra opcional
Añade un método `mediaProducciones(List<Integer> producciones)` que devuelva la media (con decimales) de todas las producciones. ¿Qué harías con la lista vacía para no dividir entre cero? ¿Qué tipo de dato devuelve ese método?
