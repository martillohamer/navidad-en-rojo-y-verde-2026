# Día 13 - El control de acceso al centro de datos

## 🎄 La historia
El centro de datos del Polo Norte guarda el historical de todos los niños naughty y nice de la lista. No todo el mundo puede entrar: cada tipo de duende tiene un nivel de seguridad asignado. Un duende **junior** todavía está en prácticas y necesita un pase de nivel alto; los demás se valen con el nivel básico. Si el pase no llega al nivel exigido, la puerta se cierra y el duende recibe un aviso: `AccesoDenegadoException`. Tu trabajo es escribir el portero del centro de datos.

## 🧠 Piensa antes de programar
- Un duende `junior` intenta entrar con un pase de nivel `1`: ¿entra o se le rechaza? ¿Qué nivel necesitaría como mínimo?
- ¿Y un duende de cualquier otro tipo (por ejemplo `experto`) con nivel `1`? ¿Entra?
- Si el nivel del pase es **exactamente** el nivel requerido, ¿se le deja pasar? (Piensa en la puerta: llegar al mínimo es suficiente.)
- ¿Qué ventaja tiene crear tu propia excepción en vez de usar un `IllegalArgumentException` genérico? ¿Cómo se diferencia "denegar el acceso" de "el dato está mal escrito"?

## ✍️ Pseudocódigo guiado
1. Calcula el nivel que exige el tipo de duende:
   - Si es `junior` → necesita nivel `3`.
   - Si es cualquier otro tipo → necesita nivel `1`.
2. Si el nivel del pase es **menor** que el nivel exigido, lanza `AccesoDenegadoException` con un mensaje claro que diga a quién se le ha denegado y con qué nivel.
3. Si el pase es suficiente, no hay nada que hacer: el método termina sin errores (no devuelve nada).

## 💡 Pista oculta
<details>
<summary>Haz clic para ver la pista</summary>
La excepción `AccesoDenegadoException` ya existe en `src/main/java/reto/dia13/AccesoDenegadoException.java` y hereda de `RuntimeException`, así que no hace falta declararla en la firma del método ni usar `throws`.
Para comparar el tipo de duende sin miedo a las mayúsculas: `"junior".equalsIgnoreCase(tipoDuende)`.
Y recuerda: denegar es cuando el nivel es **menor** que el exigido, así que la comparación es `nivelPermiso < requerido`.
</details>

## 📂 Dónde programar
Abre `src/main/java/reto/dia13/ControlAcceso.java` y escribe ahí tu solución. El test está en `src/test/java/reto/dia13/Dia13Test.java`: léelo para ver qué se espera exactamente (no hace falta modificarlo).

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia13Test
```
Verás 1 test en rojo 🔴. Tu misión: ponerlo en verde 🟢.

## 🚀 Reto extra opcional
Añade un tercer nivel de duende: los `becarios` necesitan nivel `2`. Cuando cambien las reglas, ¿qué parte del código tienes que tocar? ¿Se te ocurre cómo evitar que cada nuevo tipo añada un `if` más?
