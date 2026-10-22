# Día 23 - La compresión de historiales

## 🎄 La historia
En el sótano del Polo Norte hay un archivo con **décadas de historiales de regalos**: listas larguísimas donde el mismo dato se repite una y otra vez (`aaaaabbbb...`). Ocupan tanto que los discos mágicos de los elfos se están llenando. La archivera jefa ha oído hablar de la **compresión RLE** (_Run-Length Encoding_): en vez de guardar cada carácter, se guarda el carácter seguido de cuántas veces se repite (`aaaaa` se convierte en `a5`). Tu misión es construir el `CompresorRegalos`, que comprime un historial y lo recupera **exactamente igual** al descomprimirlo.

## 🧠 Piensa antes de programar
- En `aaabbcccc`, ¿cuántos bloques hay? ¿Cómo detectas que un carácter se repite sin salirte de la cadena?
- ¿Qué formato usas para cada bloque? Si escribes `a5`, ¿cómo distingues el número `5` del carácter `'5'` cuando el texto original contiene dígitos?
- ¿Qué pasa si el texto original es `111222333`? Sin protección, `1` repetido 3 veces se escribiría `13` y al descomprimir creerías que `1` se repite **trece** veces. ¿Cómo lo resuelves?
- ¿Qué debe devolver el compresor con una cadena `null` o vacía? ¿Y qué formato tiene una cadena de **un** carácter sin repetición?
- La propiedad clave es `descomprimir(comprimir(texto)) == texto`. ¿Por qué es tan importante probarla con textos que contienen dígitos y símbolos, no solo letras?

## ✍️ Pseudocódigo guiado
1. Si el texto es `null` o vacío, devuélvelo tal cual (comprimir el vacío es el vacío).
2. **Comprimir**: recorre el texto con un índice. Por cada carácter nuevo, cuenta cuántas veces seguidas se repite y escribe un bloque `<carácter escapado><cuenta>`.
3. **Escapar**: si el carácter es un dígito o la propia barra invertida `\`, escríbelo precedido de `\` para que no se confunda con la cuenta.
4. **Descomprimir**: recorre la cadena comprimida. Si ves `\`, el carácter literal es el siguiente; si no, el carácter es tal cual. Después, lee el número de repeticiones que viene a continuación y repite el carácter esas veces.
5. Si la entrada comprimida está mal formada (falta el número), lanza `IllegalArgumentException`.

## 💡 Pista oculta
<details>
<summary>Haz clic para ver la pista</summary>
El corazón del compresor es contar repeticiones **seguidas**: empieza en un índice `i`, cuenta con un bucle interno `while (i + veces < n && texto.charAt(i + veces) == actual)` y avanza `i += veces` al terminar el bloque.
Usa `String.repeat(veces)` (Java 11+) para reconstruir: `String.valueOf(c).repeat(veces)` es mucho más claro que un bucle que concatena.
El **escape** es lo que hace robusto el formato. Comprueba si el carácter está entre `'0'` y `'9'` (o es `\`) y, si lo está, antepón `\`. Así `123` (un dígito repetido) comprime a `\13` y se descomprime sin ambigüedad.
Para leer el número al descomprimir, recorre los dígitos desde la posición actual hasta el primer no-dígito y usa `Integer.parseInt(...)` sobre ese tramo.
`StringBuilder` para ir montando la salida sin crear un `String` nuevo por cada bloque.
</details>

## 📂 Dónde programar
Abre `src/main/java/reto/dia23/CompresorRegalos.java` y escribe ahí tu solución. El test está en `src/test/java/reto/dia23/Dia23Test.java`: léelo para ver qué se espera exactamente (no hace falta modificarlo).

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia23Test
```
Verás 9 tests en rojo 🔴. Tu misión: ponerlos en verde 🟢.

## 🚀 Reto extra opcional
El RLE solo comprime bien cuando hay repeticiones seguidas; un texto variado como `"el trineo de Santa"` casi no se reduce. La verdadera compresión usa **diccionarios**: en lugar de repetir cada palabra, se guarda una vez y se sustituye por un índice. Investiga dos clásicos: la compresión **LZ** (construir un diccionario de secuencias vistas) y la codificación de **Huffman** (dar códigos más cortos a los caracteres más frecuentes). Elige una, escribe un test de reversibilidad y comprueba cuánto se reduce un texto con palabras repetidas. Escribe primero el test nuevo: primero el rojo, luego el verde.
