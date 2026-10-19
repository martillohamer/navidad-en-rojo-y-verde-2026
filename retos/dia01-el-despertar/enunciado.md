# Día 1 – El despertar

## 🎅 La historia
En el Polo Norte, el sistema central de los elfos se ha despertado de su letargo invernal. Sin embargo, al arrancar, el altavoz principal solo emite silencio porque falta la función de saludo navideño. ¡Ayuda a los elfos a saludar a cada visitante!

## 🧠 Piensa antes de programar
- ¿Qué formato exacto debe tener el saludo si el nombre es "María"?
- ¿Cómo se concatenan cadenas de texto en Java?

## ✍️ Pseudocódigo guiado
1. Recibir el nombre como parámetro.
2. Construir la cadena uniendo `"¡Feliz Navidad, "`, el nombre y `"!"`.
3. Devolver la cadena resultante.

## 💡 Pista
<details>
<summary>Haz clic para ver la pista</summary>
Puedes usar el operador `+` para unir cadenas de texto en Java: `"Hola " + nombre`
</details>

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia01Test
```

## 🚀 Reto extra opcional
Haz que si el nombre es nulo o está vacío, devuelva `"¡Feliz Navidad, invitado misterioso!"`.
