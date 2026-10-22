# Día 11 - La carta perdida del buzón central

## 🎄 La historia
Una carta de un niño se ha perdido entre los sacos del buzón central del Polo Norte. El texto llegó mezclado con otros: "Hola Santa, vivo en Madrid con CP 28001 y me porto bien." Santa no puede leerlo todo a mano, así que necesita un analizador que Localice **dónde está el código postal** dentro del texto para que el regalo llegue a su casa. Un código postal son exactamente cinco dígitos, y el analizador no se debe equivocar ni con los números de teléfono ni con las fechas.

## 🧠 Piensa antes de programar
- ¿Cuántos dígitos tiene un código postal español? ¿Y cómo te aseguras de que el número que has encontrado es **exactamente** de 5 cifras?
- Si la carta contiene `280012` (seis dígitos) o `12345678`, ¿debería el analizador devolver `28001` o `12345`? ¿Por qué?
- ¿Qué debe devolver el método si la carta no menciona ningún código postal? (Pista: devolver `null` es una respuesta válida, no es un error.)
- ¿Por qué conviene compilar el patrón una sola vez y guardarlo en una constante `static final` en vez de escribirlo dentro del método?

## ✍️ Pseudocódigo guiado
1. Define el patrón de un código postal: cinco dígitos que no formen parte de un número más largo.
2. Crea un `Matcher` con el texto de la carta.
3. Si el `Matcher` encuentra una coincidencia (`find`), devuelve el texto encontrado (`group`).
4. Si no encuentra ninguna, devuelve `null`.

## 💡 Pista oculta
<details>
<summary>Haz clic para ver la pista</summary>
El patrón es `"\\b\\d{5}\\b"`. La parte `\\d{5}` busca cinco dígitos seguidos y los `\\b` (límites de palabra) exigen que el número no forme parte de uno más largo.
Con `find()` buscas la primera coincidencia dentro del texto (no hace falta que el texto **entero** sea un código postal); con `group()` obtienes lo que ha encontrado. Para guardar el patrón: `private static final Pattern CODIGO_POSTAL = Pattern.compile("\\b\\d{5}\\b");`
</details>

## 📂 Dónde programar
Abre `src/main/java/reto/dia11/AnalizadorCartas.java` y escribe ahí tu solución. El test está en `src/test/java/reto/dia11/Dia11Test.java`: léelo para ver qué se espera exactamente (no hace falta modificarlo).

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia11Test
```
Verás 1 test en rojo 🔴. Tu misión: ponerlo en verde 🟢.

## 🚀 Reto extra opcional
Haz que el analizador devuelva **todos** los códigos postales de la carta, en el orden en que aparecen, usando `find()` dentro de un bucle y `group()` en cada vuelta. ¿Qué peligro tiene si olvidas mover el `Matcher` de sitio?
