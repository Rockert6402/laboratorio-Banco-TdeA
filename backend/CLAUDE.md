# Backend

Las reglas de código están en [.claude/rules/springboot.md](../.claude/rules/springboot.md); no se duplican aquí.

## Comandos (desde `backend/`)

- `./gradlew build`: compila, ejecuta los tests y empaqueta.
- `./gradlew bootRun`: levanta la aplicación.
- `./gradlew test`: solo tests.
- `./gradlew test --tests "com.tdea.apptdea.NombreTest"`: un test concreto.
- `./gradlew clean`: borra `build/`.

En Windows (PowerShell/cmd) usar `.\gradlew.bat` en lugar de `./gradlew`.

## Perfiles y base de datos

PostgreSQL en `localhost:5432`, base de datos `banktdea` (debe existir antes de arrancar; `ddl-auto=update` crea las tablas, no la base).

| Perfil | Archivo | Credenciales por defecto | Arranque |
|---|---|---|---|
| default | `application.properties` | `${DB_USER:postgres}` / `${DB_PASSWORD:root}` | `./gradlew bootRun` |
| local | `application-local.properties` (no versionado) | `${DB_USER:hr}` / `${DB_PASSWORD:1204}` | `./gradlew bootRun --args='--spring.profiles.active=local'` |

- Para el perfil `local`, copiar `src/main/resources/application-local.properties.example` como `application-local.properties` y poner credenciales propias.
- Las variables de entorno `DB_USER` y `DB_PASSWORD` tienen prioridad sobre los valores por defecto de cualquier perfil. En PowerShell: `$env:DB_USER="hr"; $env:DB_PASSWORD="..."; .\gradlew.bat bootRun`.
- Conexión correcta: en el log aparece `HikariPool-1 - Start completed.` y `Started ApptdeaApplication`.

## Puerto y CORS

- `SERVER_PORT` (por defecto 8080) y `CORS_ALLOWED_ORIGINS` (por defecto `http://localhost:3000`, separados por coma).
- Si 8080/3000 están ocupados por otro proyecto, ejemplo con el frontend en 3002:
  ```bash
  DB_USER=postgres DB_PASSWORD=... SERVER_PORT=8090 CORS_ALLOWED_ORIGINS=http://localhost:3002 ./gradlew bootRun
  ```
  y en el frontend: `VITE_API_URL=http://localhost:8090/api npx vite --port 3002`.
