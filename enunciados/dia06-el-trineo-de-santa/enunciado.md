# Día 6 – El trineo de Santa

## 🎅 La historia
La víspera de Nochebuena, Santa carga el trineo y tiene que decidir en qué orden reparte cada regalo. Los pequeños caben en cualquier hueco, pero los pesados van abajo. Y los frágiles necesitan trato especial. Tu función recibe el peso del regalo y si es frágil, y responde con una etiqueta para la carga.

## 🧠 Piensa antes de programar
- ¿A partir de qué peso consideras que un regalo es pesado? Aquí el límite es **5 kg**: por debajo es liviano, de 5 en adelante es pesado.
- ¿Cuántas combinaciones posibles hay entre "liviano o pesado" y "frágil o no frágil"?
- ¿Cómo dibujarías esto en un papel con flechas, como un mapa de decisiones?
- Es como elegir paquete en una mensajería: no es lo mismo enviar un libro que una lámpara de cristal.

## ✍️ Pseudocódigo guiado
Estas son las cuatro respuestas posibles:

| Peso | ¿Frágil? | Respuesta |
|---|---|---|
| menos de 5 kg | sí | `LIVIANO-ALTA` |
| menos de 5 kg | no | `LIVIANO-NORMAL` |
| 5 kg o más | sí | `PESADO-URGENTE` |
| 5 kg o más | no | `PESADO-NORMAL` |

1. Primero decide si el regalo es liviano o pesado.
2. Dentro de cada caso, decide la prioridad según sea frágil o no.
3. Devuelve el texto exacto de la tabla.

## 💡 Pista
<details>
<summary>Haz clic para ver la pista</summary>
Puedes anidar un `if` dentro de otro. Para decisiones de dos opciones también existe el operador ternario: `esFragil ? "ALTA" : "NORMAL"`.
</details>

## 📂 Dónde programar
Abre `src/main/java/reto/dia06/TrineoSanta.java` y escribe ahí tu solución. El test está en `src/test/java/reto/dia06/Dia06Test.java`: léelo para ver qué se espera exactamente (no hace falta modificarlo).

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia06Test
```
Verás 2 tests en rojo 🔴. Tu misión: ponerlos en verde 🟢.

## 🚀 Reto extra opcional
Añade una categoría nueva para los regalos de más de 20 kg. ¿Cómo cambia tu estructura de `if`? ¿Prefieres reescribirla de otra forma?
