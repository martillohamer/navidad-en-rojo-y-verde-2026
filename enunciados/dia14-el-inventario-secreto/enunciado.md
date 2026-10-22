# Día 14 - El inventario secreto

## 🎄 La historia
En el almacén del Polo Norte, cada regalo tiene su ficha en una línea de texto: nombre, stock y categoría, todo separado por comas. Así de simple: `tren,150,juguetes`. Cada noche hay que comprobar que las cifras del almacén cuadran con las de la fábrica, y para eso hace falta leer **el stock** de cada línea sin tener que abrir 3000 fichas a mano. Hoy te toca escribir el parser que extrae ese número de un texto plano.

## 🧠 Piensa antes de programar
- En `tren,150,juguetes`, ¿en qué posición está el stock y por qué? ¿Cuántas partes tiene la línea al dividirla por comas?
- ¿Qué debería ocurrir si llega una línea incompleta, como solo `tren`? ¿Devuelves `0` o informs del error? ¿Qué excepción usarías para avisar de que la línea está mal formada?
- ¿Y si el número llega con espacios alrededor, como `tren, 150 , juguetes`? ¿Por qué conviene quitar los espacios antes de convertir el texto a número?
- ¿Qué pasaría si el stock no es un número, sino la palabra `muchos`? ¿Qué excepción te da Java al intentarlo?

## ✍️ Pseudocódigo guiado
1. Divide la línea por las comas en sus partes.
2. Si no hay al menos **dos** partes, la línea está incompleta: lanza `IllegalArgumentException` con un mensaje que incluya la línea recibida.
3. Toma la segunda parte (el stock) y quítale los espacios sobrantes.
4. Conviértela a número entero y devuélvela.

## 💡 Pista oculta
<details>
<summary>Haz clic para ver la pista</summary>
Con `String[] partes = lineaCsv.split(",")` tienes cada columna separada. El stock es `partes[1]` (el índice empieza en 0) y `partes[1].trim()` te quita los espacios.
Cuidado con un detalle de `split`: los campos **vacíos al final** no se cuentan, así que `"tren,150,"` produce solo 2 partes, no 3.
Y para el error: `throw new IllegalArgumentException("Linea CSV incompleta: " + lineaCsv)`.
</details>

## 📂 Dónde programar
Abre `src/main/java/reto/dia14/ParserInventario.java` y escribe ahí tu solución. El test está en `src/test/java/reto/dia14/Dia14Test.java`: léelo para ver qué se espera exactamente (no hace falta modificarlo).

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia14Test
```
Verás 1 test en rojo 🔴. Tu misión: ponerlo en verde 🟢.

## 🚀 Reto extra opcional
Añade un método `extraerCategoria(String lineaCsv)` que devuelva la tercera columna del CSV. ¿Qué error hay que prever si la línea solo tiene dos columnas? Escribe antes el test que lo compruebe: eso es TDD.
