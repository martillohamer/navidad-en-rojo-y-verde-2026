# Día 18 - El cifrador polar

## 🎄 La historia
En el Polo Norte los elfos se comunican con mensajes secretos: si un espía interceptara la lista de regalos, la Nochebuena estaría perdida. Para protegerse, el gremio de criptógrafos elfos usa un cifrador muy antiguo, primo del **cifrado César**: cada letra del mensaje se desplaza un número fijo de posiciones dentro del alfabeto y, al llegar a la `z`, vuelve a empezar por la `a`. Los espacios, los números y los signos viajan tal cual, para que el mensaje siga siendo legible una vez descifrado. Tu misión es construir el `CifradorPolar` que cifra y descifra estos mensajes con un desplazamiento configurable.

## 🧠 Piensa antes de programar
- Si desplazas la `a` una posición, ¿en qué letra se convierte? ¿Y la `z`? ¿Qué pasa cuando el desplazamiento empuja una letra **más allá de la última**?
- ¿Se deben tratar igual la `a` (minúscula) y la `A` (mayúscula)? ¿Qué tiene que ocurrir con el resultado para no perder esa distinción?
- Si el mensaje dice `"Hola, mundo!"`, ¿qué debería ocurrir con la coma, el espacio y la exclamación? ¿Los ciframos también?
- ¿Qué pasa con un desplazamiento **negativo**, como `-1`? ¿Y con uno mayor que el alfabeto, como `27`? ¿Cuál es el desplazamiento "real" en cada caso?
- Si cifro un texto con desplazamiento `5` y luego lo descifro con `5`, ¿recupero el original? ¿Cómo se relacionan ambos métodos?

## ✍️ Pseudocódigo guiado
1. `cifrar(texto, desplazamiento)` y `descifrar(texto, desplazamiento)` comparten el mismo motor de transformación: descifrar es cifrar con el desplazamiento **de signo contrario**.
2. Si el texto es `null`, devuelve `null`. Si el desplazamiento es enorme o negativo, **normalízalo** al rango `0..25` con el módulo.
3. Recorre el texto carácter a carácter. Por cada letra minúscula, gírala dentro de `a..z`; por cada mayúscula, dentro de `A..Z`; para cualquier otro carácter, cópialo sin cambios.
4. Para girar una letra: calcula su posición (`c - base`), súmale el desplazamiento, aplícale el módulo `26` y vuelve a la letra sumando la base.
5. Devuelve el texto transformado.

## 💡 Pista oculta
<details>
<summary>Haz clic para ver la pista</summary>
Normaliza el desplazamiento una sola vez: `int giro = ((desplazamiento % 26) + 26) % 26;`. Con eso resuelves tramos negativos y valores gigantes sin casos especiales.
Compara rangos numéricos en vez de usar `Character.isLetter(...)`, porque `isLetter` también acepta letras acentuadas o de otros alfabetos que aquí no queremos tocar: usa `if (c >= 'a' && c <= 'z')` y `if (c >= 'A' && c <= 'Z')`.
El truco del sobre: en Java, `char` es un número, así que `(char) ('a' + nuevaPosicion)` reconstruye la letra. Para las mayúsculas, la base es `'A'` y el rango `'A'`–`'Z'`.
Usa `StringBuilder` para construir el resultado carácter a carácter; concatenar con `+` dentro de un bucle escribe un `String` nuevo en cada vuelta.
</details>

## 📂 Dónde programar
Abre `src/main/java/reto/dia18/CifradorPolar.java` y escribe ahí tu solución. El test está en `src/test/java/reto/dia18/Dia18Test.java`: léelo para ver qué se espera exactamente (no hace falta modificarlo).

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia18Test
```
Verás 9 tests en rojo 🔴. Tu misión: ponerlos en verde 🟢.

## 🚀 Reto extra opcional
Un verdadero cifrador no usa siempre el mismo desplazamiento: sería demasiado fácil de romper. Añade una variante `cifrarConClave(String texto, String clave)` en la que cada letra avance el número de posiciones que marque la letra correspondiente de la clave (`a = 1`, `b = 2`, ...). Si la clave se acaba antes que el texto, vuelve a empezar por el principio. ¿Cómo reutilizarías tu método `rotar` para no repetir la lógica de giro? Escribe primero el test nuevo: primero el rojo, luego el verde.
