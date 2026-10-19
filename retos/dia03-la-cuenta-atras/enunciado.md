# Día 3 – La cuenta atrás

## 🎅 La historia
Faltan pocos días para Navidad y los niños en el taller del Polo Norte necesitan ver la cuenta atrás en la gran pantalla. Si faltan 5 días, debe mostrar: `"5, 4, 3, 2, 1, ¡Navidad!"`.

## 🧠 Piensa antes de programar
- ¿Cómo podemos recorrer desde un número dado hasta 1 usando un bucle?
- ¿Cómo acumulamos los números separados por comas seguidos de `" ¡Navidad!"` al final?
- ¿Qué pasa si `dias` es 0 o negativo?

## ✍️ Pseudocódigo guiado
1. Si `dias <= 0`, devuelve `"¡Navidad!"`.
2. Crea un acumulador o `StringBuilder`.
3. Haz un bucle desde `dias` hasta 1, añadiendo cada número y una coma.
4. Al final, añade `"¡Navidad!"`.

## 💡 Pista
<details>
<summary>Haz clic para ver la pista</summary>
Puedes usar un bucle `for` decreciente: `for (int i = dias; i >= 1; i--)`
</details>

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia03Test
```

## 🚀 Reto extra opcional
Asegúrate de que no queden espacios o comas sobrantes al final.
