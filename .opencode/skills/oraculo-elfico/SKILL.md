---
name: oraculo-elfico
description: Use when the user asks for a magical North Pole letter, gift verdict, or "oráculo élfico" decision for a child. Genera cartas y decisiones mágicas conectando OpenCode con la instancia local de Ollama (llama3, http://localhost:11434/api/generate).
---

# Oráculo Élfico

Conecta OpenCode con la instancia local de Ollama para generar cartas navideñas
y decisiones mágicas personalizadas, reutilizando el cliente Java del proyecto
(`reto.extension.oraculo.OllamaOraculoClient`).

## Cuándo usar esta skill

Actívala cuando el usuario pida:
- Generar una carta navideña para un niño/a.
- Un veredicto o decisión del "oráculo élfico" (¿merece regalo? ¿carbón?).
- Texto mágico conectado con Ollama local.

## Entradas (parámetros)

| Parámetro | Tipo | Descripción |
| --- | --- | --- |
| `nombreNino` | string | Nombre del niño |
| `comportamiento` | string | Evaluación de comportamiento |
| `regaloDeseado` | string | Objeto o juguete solicitado |

Los tres son obligatorios.

## Puente operativo

- **Endpoint:** `http://localhost:11434/api/generate`
- **Modelo:** `llama3`
- **Cliente del proyecto:** `src/main/java/reto/extension/oraculo/OllamaOraculoClient.java`
  (gemelo byte-idéntico en `src/solutions/java/reto/extension/oraculo/`).

## Cómo usarla

1. Requiere que Ollama esté corriendo en local con el modelo `llama3`:
   ```bash
   ollama run llama3
   ```
2. Compón el prompt con las tres entradas, por ejemplo:
   ```text
   Escribe una breve carta navideña de Papa Noel para <nombreNino>.
   Comportamiento este año: <comportamiento>.
   Pidió: <regaloDeseado>.
   ```
3. Invoca el cliente Java (o equivalentemente `POST` a la API de Ollama):
   ```bash
   curl -s http://localhost:11434/api/generate \
     -d '{"model":"llama3","prompt":"<prompt>","stream":false}'
   ```
4. Con el cliente del proyecto:
   ```java
   String carta = new OllamaOraculoClient().generarCarta(nombreNino, regaloDeseado);
   ```

## Manejo de errores

- Si `generarCarta` lanza `OllamaOraculoException`, informa de que Ollama no está
  disponible en `localhost:11434` y sugiere arrancarlo con `ollama run llama3`.
- Nunca expongas claves ni datos sensibles en la carta.
