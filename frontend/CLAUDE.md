# Frontend

Las reglas de código están en [.claude/rules/react.md](../.claude/rules/react.md); no se duplican aquí.

## Comandos (desde `frontend/`)

- `npm install`: instala dependencias.
- `npm run dev`: servidor de desarrollo en http://localhost:3000 (falla si el puerto está ocupado).
- `npm run lint`: lint con oxlint.
- `npm run build`: build de producción en `dist/`.
- `npm run preview`: sirve el build en el puerto 3000.

## Configuración

Copiar `.env.example` como `.env`. `VITE_API_URL` apunta a la API del backend (por defecto `http://localhost:8080/api`).

Si el puerto 3000 está ocupado, se puede usar otro sin tocar la configuración (la variable de entorno tiene prioridad sobre `.env`):
`VITE_API_URL=http://localhost:8090/api npx vite --port 3002`. El backend debe permitir ese origen en `CORS_ALLOWED_ORIGINS`.
