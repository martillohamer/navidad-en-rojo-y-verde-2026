# Día 15 - La flota de trineos

## 🎄 La historia
El Polo Norte no tiene un trineo, tiene una **flota**. Y para poder repararlos, conducirlos y programarlos todos igual, Santa ha fijado un contrato: cualquier trineo de la flota, sin importar su motor, debe saber decir cuál es su velocidad máxima. El contrato ya existe: es la interfaz `Trineo`. Hoy te toca construir el **Trineo Mágico**, el más rápido de la flota, cuyo motor duplica la potencia base que le dan en la fábrica.

## 🧠 Piensa antes de programar
- ¿Qué garantiza la interfaz `Trineo` a cualquier trineo de la flota? ¿Qué métodos deben tener todos?
- Con una potencia base de `50`, ¿qué velocidad máxima da el trineo mágico? ¿De dónde sale ese número?
- ¿Por qué `TrineoMagico` implementa `Trineo` en vez de simplemente tener un método `calcularVelocidadMax()`? (Pista: piensa en una lista con trineos de distintos tipos.)
- ¿Qué pasaría si el método devolviera un `double` en lugar de un `int`? ¿Seguiría cumpliendo el contrato?

## ✍️ Pseudocódigo guiado
1. Guarda la potencia base que llega por el constructor en un atributo `private final int potenciaBase`.
2. Implementa el método `calcularVelocidadMax()` de la interfaz `Trineo` (no olvides la anotación `@Override`).
3. La magia del trineo consiste en **duplicar** la potencia base: devuelve `potenciaBase * 2`.
4. Así, todos los trineos de la flota respetan el mismo contrato y se pueden usar intercambiables en el mismo código.

## 💡 Pista oculta
<details>
<summary>Haz clic para ver la pista</summary>
La interfaz `Trineo` ya está creada en `src/main/java/reto/dia15/Trineo.java` con un único método `int calcularVelocidadMax();`: no la modifiques, solo implementa ese método.
La declaración de la clase debe incluir `implements Trineo`, y el método que cumple el contrato se marca con `@Override`.
Con `private final` el atributo se lee desde fuera solo mediante el constructor y no puede cambiarse por error: el trineo es inmutable.
</details>

## 📂 Dónde programar
Abre `src/main/java/reto/dia15/TrineoMagico.java` y escribe ahí tu solución. El test está en `src/test/java/reto/dia15/Dia15Test.java`: léelo para ver qué se espera exactamente (no hace falta modificarlo).

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia15Test
```
Verás 1 test en rojo 🔴. Tu misión: ponerlo en verde 🟢.

## 🚀 Reto extra opcional
Crea una segunda clase `TrineoBasico` que también implemente `Trineo`, pero cuya velocidad máxima sea la mitad de la potencia base. Escribe después una lista `List<Trineo>` con los dos trineos y recórrela. ¿Qué ventaja te da que el test declare la variable como `Trineo` y no como `TrineoMagico`?
