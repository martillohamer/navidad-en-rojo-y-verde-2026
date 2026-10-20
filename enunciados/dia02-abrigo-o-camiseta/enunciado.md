# Día 2 – Abrigo o camiseta

## 🎅 La historia
Los elfos están preparando sus maletas para repartir regalos por todo el mundo. Elfo Carlos va a Oviedo (5 °C) y Elfo Lucía va a Asunción (35 °C). Necesitan un programa que les diga qué ropa llevar según la temperatura.

## 🧠 Piensa antes de programar
- Si hace frío (menos de 15 °C), ¿qué se necesita?
- ¿Y si hace calor (25 °C o más)?
- ¿Qué pasa entre medias?

## ✍️ Pseudocódigo guiado
1. Si `grados < 15`, devuelve `"abrigo"`.
2. Si `grados >= 25`, devuelve `"camiseta"`.
3. Si está entre 15 y 24 (inclusive), devuelve `"jersey"`.

## 💡 Pista
<details>
<summary>Haz clic para ver la pista</summary>
Usa estructuras condicionales `if-else if-else` en Java.
</details>

> Programa tu solución en `src/main/java/reto/dia02/` y consulta el test en `src/test/java/reto/dia02/` para conocer los casos límite y las excepciones esperadas.

## 🧪 Cómo comprobarlo
Ejecuta en tu terminal:
```bash
mvn test -Dtest=Dia02Test
```

## 🚀 Reto extra opcional
Añade temperaturas bajo cero (ej: -5 °C) que devuelvan `"plumas y bufanda"`.
