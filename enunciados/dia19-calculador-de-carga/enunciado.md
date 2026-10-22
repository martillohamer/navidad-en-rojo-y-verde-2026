# Día 19 - El calculador de carga del trineo

## 🎄 La historia
En la nave de carga del Polo Norte hay una báscula mágica que pesa cada regalo antes de subirlo al trineo. Santa tiene un límite claro: ni un gramo de más, porque el trineo no despega si va sobrecargado. Los duendes van apilando regalos uno a uno en la báscula, que suma sus pesos en tiempo real. Tu misión es construir el `CalculadorCarga`: recibe el peso máximo que aguanta el trineo en su construcción, y cada vez que se apila la lista de regalos devuelve el peso total o da la alarma `ExcesoCapacidadException` en cuanto el montón se pasa del límite.

## 🧠 Piensa antes de programar
- Si el trineo aguanta `20 kg` y los regalos pesan `10 kg` y `15 kg`, ¿cuándo hay que dar la alarma: al terminar la lista o **en cuanto** el acumulado supera el límite?
- ¿Qué debe devolver el método si la lista de regalos llega `null` o vacía? ¿Es un error o simplemente no hay nada cargado?
- Si el peso total cae **exactamente** sobre el límite, ¿es válido o hay exceso? Fíjate en si hablamos de "superar" o de "alcanzar".
- El peso es decimal (`double`). ¿Cómo comprobarías en un test que dos pesos son iguales sin exigir una igualdad exacta?
- ¿Por qué tiene sentido guardar `pesoMaximo` en el **constructor** y no pasarlo como parámetro en cada llamada?

## ✍️ Pseudocódigo guiado
1. El constructor recibe `pesoMaximo`. Si es negativo, lanza `IllegalArgumentException`: un trineo no puede aguantar "menos que nada".
2. En `calcularPesoTotal(List<Regalo> regalos)`, si la lista es `null` o está vacía, devuelve `0.0`.
3. Recorre los regalos con un acumulador `total = 0.0` y ve sumando el peso de cada uno (`regalo.peso()`).
4. Después de **cada** suma, comprueba si `total > pesoMaximo`. Si se pasa, lanza `ExcesoCapacidadException` con un mensaje que explique el exceso.
5. Si terminas la lista sin pasarte, devuelve el `total`.

## 💡 Pista oculta
<details>
<summary>Haz clic para ver la pista</summary>
Un `record Regalo(String nombre, double peso)` te da el constructor y el accesor `peso()` sin escribir nada más. Dentro del método se lee el peso con `regalo.peso()`, no con un `getPeso()`.
Compara con `total > pesoMaximo` (estrictamente mayor): así, aterrizar justo en el límite es válido. El test lo comprueba explícitamente.
Lanza la excepción **dentro del bucle**, no al final: si el primer regalo ya se pasa, el método debe avisar de inmediato sin seguir sumando.
Recuerda crear tu propia excecpión extendiendo `RuntimeException`, igual que hicieron los días 4 y 5 con sus excepciones de taller y cinta.
En los tests usa `assertThrows(ExcesoCapacidadException.class, () -> ...)` y compara `double` con un margen (`delta`), no con `==`.
</details>

## 📂 Dónde programar
Abre `src/main/java/reto/dia19/CalculadorCarga.java` y escribe ahí tu solución. La excepción vive en `src/main/java/reto/dia19/ExcesoCapacidadException.java`. El test está en `src/test/java/reto/dia19/Dia19Test.java`: léelo para ver qué se espera exactamente (no hace falta modificarlo).

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia19Test
```
Verás 6 tests en rojo 🔴. Tu misión: ponerlos en verde 🟢.

## 🚀 Reto extra opcional
La báscula ya avisa cuando el trineo se sobrecarga, pero Santa quiere repartir mejor: añade un método `cargarOptimo(List<Regalo> regalos)` que devuelva la combinación de regalos cuyo peso total sea el **mayor posible sin pasarse** del `pesoMaximo` (el clásico *problema de la mochila*). Con pocos regalos, ¿probarías todas las combinaciones? Empieza por el caso más simple y añade tests: primero el rojo, luego el verde.
