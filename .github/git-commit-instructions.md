# Instrucciones estrictas para mensajes de commit en SYLPH

Por favor, sigue estas reglas estrictas para cada mensaje de commit:

1. **Formato del mensaje**
   - El mensaje debe estar en español.
   - La primera línea debe ser un resumen breve y conciso (máximo 72 caracteres, sin punto final).
   - Deja una línea en blanco después del resumen.
   - El cuerpo (opcional) debe explicar el "qué" y el "por qué" del cambio, no el "cómo".
   - Si el commit cierra un issue, referencia el número (por ejemplo, `Closes #123`).

2. **Estilo y contenido**
   - Usa el modo imperativo en el resumen (ejemplo: "Agrega soporte para mailboxes prioritarios").
   - No uses mayúsculas innecesarias ni emojis.
   - No incluyas información irrelevante o genérica (como "cambios varios").
   - Sé específico: describe claramente la funcionalidad, corrección o refactorización.
   - Si el cambio es una corrección, comienza con "Corrige ...".
   - Si es una nueva funcionalidad, comienza con "Agrega ..." o "Implementa ...".
   - Si es una refactorización, comienza con "Refactoriza ...".
   - Si es documentación, comienza con "Documenta ...".

3. **Ejemplos válidos**
   - Agrega soporte para mailboxes plugin-based
   - Corrige error de concurrencia en BasicActor
   - Refactoriza ActorSystem para mejorar legibilidad
   - Documenta uso de PriorityMailbox en ejemplos

4. **Ejemplos inválidos**
   - cambios varios
   - Update code
   - fix
   - .
   - Cambios

5. **Rechaza el commit si no cumple estas reglas**
   - Si el mensaje no sigue estas reglas, corrígelo antes de hacer commit.

---

**Resumen:**
- Sé claro, específico y profesional.
- Usa el formato y estilo indicados.
- Explica el propósito del cambio.
- No aceptes mensajes genéricos o poco descriptivos.

