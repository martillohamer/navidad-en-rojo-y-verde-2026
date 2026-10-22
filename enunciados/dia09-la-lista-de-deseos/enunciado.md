# Día 9 - La lista de deseos de los niños

## 🎄 La historia
Cada noche, miles de cartas de niños llegan al buzón del Polo Norte. El archivista de los deseos las lee una a una y las anota en una única lista: `tren`, `muneca`, `tren`, `pijamas`... Para preparar el almacén, necesita responder a una pregunta muy concreta: **¿cuántos niños han pedido cada regalo?** Con esa información, Santa sabe cuántos trenes y cuántas muñecas tienen que fabricar. Hoy tu tarea es contar, sin que ningún deseo se escape del recuento.

## 🧠 Piensa antes de programar
- Si tres niños piden `tren`, ¿qué tiene que devolver `resultado.get("tren")`? ¿Y si solo lo pide uno?
- ¿Por qué devuelves un `Map` y no una `List`? ¿Qué ganas al buscar por clave en vez de recorrer todo?
- Si la lista de deseos llega **vacía**, ¿qué debería devolver el método? (Pista: no es lo mismo un mapa vacío que un `null`).
- ¿Qué le ocurre al mapa si el mismo deseo aparece cinco veces? ¿SeGuardan cinco entradas o una sola con el valor 5?

## ✍️ Pseudocódigo guiado
1. Crea un mapa vacío: `new HashMap<>()`.
2. Recorre la lista de deseos, un deseo cada vez.
3. Para cada deseo, mira si ya está en el mapa:
   - Si no estaba, guarda ese deseo con el valor `1`.
   - Si ya estaba, sube su contador en `1`.
4. Devuelve el mapa con el total de cada regalo.

## 💡 Pista oculta
<details>
<summary>Haz clic para ver la pista</summary>
El método `Map.merge(clave, valor, función)` hace justo lo que necesitas: si la clave no existe, la crea con el valor indicado; si ya existe, le aplica la función al valor antiguo y al nuevo. Con `Integer::sum` te suma uno a uno de forma automática.
Alternativa sin `merge`: `conteo.put(deseo, conteo.getOrDefault(deseo, 0) + 1)`.
Y para leer el resultado del test: `resultado.get("tren")`.
</details>

## 📂 Dónde programar
Abre `src/main/java/reto/dia09/ListaDeseos.java` y escribe ahí tu solución. El test está en `src/test/java/reto/dia09/Dia09Test.java`: léelo para ver qué se espera exactamente (no hace falta modificarlo).

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia09Test
```
Verás 1 test en rojo 🔴. Tu misión: ponerlo en verde 🟢.

## 🚀 Reto extra opcional
Añade un método `deseoMasSolicitado(List<String> deseos)` que devuelva el regalo más pedido. Si hay empate, devuelve el que **apareció antes** en la lista. ¿Qué estructura te da eso gratis y por qué?
