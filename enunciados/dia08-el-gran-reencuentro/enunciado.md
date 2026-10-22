# Día 8 – El gran reencuentro

## 🎅 La historia
Es el último ensayo general antes de la gran noche. Santa quiere saber si **todo el Polo Norte funciona a la vez**: la producción del taller (día 4) y la cinta transportadora (día 5). Si cualquiera de las dos falla, la jornada no se puede dar por buena. Hoy no escribes nada desde cero: hoy juntas lo que ya has construido.

> ⚠️ Antes de empezar necesitas haber resuelto los **días 4 y 5**: este reto usa tus soluciones.

## 🧠 Piensa antes de programar
- ¿Qué dos comprobaciones hay que hacer para dar la jornada por válida?
- Cuando el taller o la cinta dan la alarma, lanzan una excepción. ¿Qué hay que devolver en ese caso?
- ¿Qué pasaría si no atrapas la excepción? ¿Qué vería el jefe del taller en pantalla?
- Es como el jefe de turno que revisa la lista de comprobación antes de abrir la fábrica: si algo falla, se anota "no" y se avisa.

## ✍️ Pseudocódigo guiado
1. Intenta (`try`) calcular la producción total con el taller.
2. Intenta simular la cinta con los paquetes y su capacidad.
3. Si las dos cosas han ido bien, devuelve `true`.
4. Si cualquiera ha lanzado su excepción, atrápala (`catch`) y devuelve `false`.

## 💡 Pista
<details>
<summary>Haz clic para ver la pista</summary>
Crea los objetos con `new TallerDuendes()` y `new CintaTransportadora()`. Puedes atrapar dos tipos de excepción a la vez: `catch (InvalidProductionException | ConveyorCapacityException e)`.
</details>

## 📂 Dónde programar
Abre `src/main/java/reto/dia08/MotorPoloNorte.java` y escribe ahí tu solución. El test está en `src/test/java/reto/dia08/Dia08Test.java`: léelo para ver qué se espera exactamente (no hace falta modificarlo).

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia08Test
```
Verás 1 test en rojo 🔴. Tu misión: ponerlo en verde 🟢.

## 🚀 Reto extra opcional
Piensa cómo harías para que el método no solo diga "sí" o "no", sino también **por qué** ha fallado la jornada. ¿Qué tipo devolverías en lugar de `boolean`?
