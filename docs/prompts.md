# Prompts del proyecto

Prompts usados con Claude Code para construir el sistema, en orden. Se transcriben tal como se escribieron.

---

## 1. Reglas del backend

```
Crea el archivo de reglas .claude/rules/springboot.md con las buenas prácticas de Java 25 para desarrollar un servicio bancario en Spring Boot 4. Estas reglas aplican siempre a todo el backend:

Nombres e idioma
- Clases, records, enums, métodos, atributos y variables en inglés. Comentarios y Javadoc en español.
- Sufijos por capa: *Controller, *Service, *Repository, *Mapper, *Exception; DTO *Request / *Response.
- Tablas y columnas en inglés con snake_case; endpoints bajo /api/<recurso>.

Documentación
- Toda clase pública y todo método público llevan Javadoc con descripción, @param, @return y @throws según aplique, actualizado en cada cambio.

Arquitectura
- Flujo Controller → Service → Repository. Controladores sin lógica de negocio.
- @Transactional en los servicios; readOnly = true en las lecturas.
- Conversión entre entidad y DTO en un *Mapper dedicado.

Dinero y transacciones (reglas críticas)
- Montos y saldos siempre con BigDecimal (precision 15, scale 2); prohibido double o float para dinero.
- Comparar montos con compareTo, nunca con equals.
- Una transferencia es atómica: el débito, el crédito y el registro de la transacción van en el mismo @Transactional; si algo falla, no se guarda nada.
- Control de concurrencia en las cuentas con @Version (bloqueo optimista).
- Las transacciones registradas nunca se modifican ni se borran.

DTO y API
- Los DTO de entrada y salida son Java records con Bean Validation. Nunca exponer entidades JPA.
- Códigos HTTP: GET 200, POST 201 con Location, 400 validación, 404 no existe, 409 regla de negocio (saldo insuficiente, misma cuenta, etc.).

Errores
- GlobalExceptionHandler (@RestControllerAdvice) con cuerpo ErrorResponse {message, timestamp}. Sin try/catch en los controladores ni stack traces al cliente.

Java 25 y Spring
- Inyección por constructor con campos private final; prohibido @Autowired en campos.
- Records, switch con patrones, var solo si el tipo es evidente, Stream.toList().
- Optional solo como retorno; orElseThrow, nunca get() sin verificar.
- Entidades sin @Data; relaciones LAZY; enums con EnumType.STRING; fechas con java.time.
- Logs con SLF4J.

Seguridad y configuración
- Credenciales en variables de entorno con valores por defecto; los archivos con credenciales personales no se suben al repositorio.

Al final incluye la estructura actual del proyecto (léela del disco) y la regla de mantenerla actualizada.
```

## 2. CLAUDE.md

```
Crea CLAUDE.md en la raíz: una línea que describa el proyecto (sistema de transferencias bancarias con QR), la importación @.claude/rules/springboot.md (y luego @.claude/rules/react.md), y la regla de que antes de terminar una tarea el backend compila (./gradlew build) y el frontend pasa lint y build.
Crea backend/CLAUDE.md corto: comandos de Gradle y una referencia a la regla, sin duplicarla.
```

## 3. PostgreSQL y perfiles

```
Configura PostgreSQL con perfiles de Spring:
- application.properties: base de datos banktdea en localhost:5432, ${DB_USER:postgres}, ${DB_PASSWORD:root}, ddl-auto=update, PostgreSQLDialect, show-sql y format_sql.
- application-local.properties (perfil local): ${DB_USER:hr} y ${DB_PASSWORD:1204}; agrégalo al .gitignore y crea un .example.
Documenta en backend/CLAUDE.md cómo arrancar con cada perfil. Arranca y confirma la conexión.
```

## 4. Entidades y datos de prueba

```
Crea las entidades JPA:
- Account (tabla "accounts"): id (Long, IDENTITY), accountNumber (String, único, 10 dígitos), ownerName (String), balance (BigDecimal, precision 15, scale 2), createdAt (LocalDateTime) y version (@Version).
- Transaction (tabla "transactions"): id, reference (String único, generado con UUID), sourceAccount y targetAccount (@ManyToOne LAZY a Account), amount (BigDecimal), description (String, opcional), createdAt (LocalDateTime) y status (enum TransactionStatus: COMPLETED, FAILED, guardado como texto).
Getters, setters y constructores (Lombok permitido, sin @Data). Javadoc en los métodos públicos.
Crea config/DataSeeder (CommandLineRunner) que inserte 4 cuentas de prueba con titulares y saldos distintos, solo si la tabla está vacía.
```

## 5. Repositorios

```
Crea:
- AccountRepository extendiendo JpaRepository<Account, Long>, con findByAccountNumber y existsByAccountNumber.
- TransactionRepository con un método que traiga las transacciones donde una cuenta es origen o destino, ordenadas por createdAt descendente; findAllByOrderByCreatedAtDesc; y findByReference.
```

## 6. Servicios, DTO, mappers y excepciones

```
Crea los servicios:
- AccountService: findAll(), findByNumber(number), create(AccountRequest) (valida que el número no exista y que el saldo inicial no sea negativo).
- TransferService.transfer(TransferRequest), todo en un solo @Transactional:
  1. El monto es mayor que 0.
  2. La cuenta origen y la destino existen (si no, 404).
  3. Origen y destino son distintos (si no, 409).
  4. El origen tiene saldo suficiente (si no, InsufficientFundsException → 409).
  5. Descuenta del origen, suma al destino y guarda la Transaction COMPLETED con su reference.
- TransactionService: findAll(), findByAccount(number) y findByReference(reference).
Records en dto/: AccountRequest, AccountResponse, TransferRequest(sourceAccountNumber, targetAccountNumber, amount, description), TransactionResponse(reference, sourceAccountNumber, sourceOwnerName, targetAccountNumber, targetOwnerName, amount, description, createdAt, status) y ErrorResponse.
Mappers en mapper/. Excepciones: BusinessRuleException, InsufficientFundsException y ResourceNotFoundException, más un GlobalExceptionHandler (400, 404 y 409, y 409 también en conflictos de @Version).
```

## 7. Controladores, Swagger y CORS

```
Agrega springdoc-openapi-starter-webmvc-ui en una versión compatible con Spring Boot 4.1 (verifícala).
Crea los controladores con @Tag y @Operation:
- AccountController: GET /api/accounts (200), GET /api/accounts/{number} (200 o 404), POST /api/accounts (201)
- TransferController: POST /api/transfers (201 con la TransactionResponse)
- TransactionController: GET /api/transactions?accountNumber= (opcional, más recientes primero) y GET /api/transactions/{reference} (200 o 404)
Crea config/CorsConfig para http://localhost:3000 en /** con GET, POST, PUT, DELETE y OPTIONS.
Compila, arranca y confirma que swagger-ui.html carga.
```

## 8. Frontend y reglas de React

```
Crea el frontend en frontend/ con Vite + React (JavaScript), en el puerto 3000 con strictPort. Instala axios y qrcode.react.

Crea el archivo de reglas .claude/rules/react.md, que aplica siempre al frontend:

Idioma
- Componentes, hooks, funciones, variables, props, archivos y clases CSS en inglés. Textos de la interfaz en español.

Estructura
- src/services: Axios sobre una instancia compartida (apiClient.js); un archivo por recurso. Los componentes nunca usan axios directamente.
- src/hooks: un hook por componente o página con lógica.
- src/components: componentes reutilizables, una carpeta por componente.
- src/pages: pantallas completas.
- src/styles: solo estilos globales (variables CSS y reset).
- src/utils: funciones puras (formato de moneda COP y de fechas).

Componentes
- Cada componente se separa en 3 archivos: lógica (hook), vista (.jsx, solo JSX) y estilos (.css propio con clases prefijadas).
- Toda vista con datos remotos cubre carga, error con "Reintentar" y lista vacía.
- Accesibilidad básica: label en cada campo y errores asociados al campo.

Dinero
- Los montos se muestran con Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' }).
- Los montos se envían al backend como número o string decimal, nunca calculados con operaciones de punto flotante en el front.

Configuración
- La URL del backend sale de import.meta.env.VITE_API_URL; .env no se sube y existe .env.example.
- Antes de terminar: npm run lint y npm run build sin errores.

Incluye la estructura real del frontend y mantenla actualizada.
Agrega @.claude/rules/react.md al CLAUDE.md de la raíz y crea frontend/CLAUDE.md corto con los comandos.
```

## 9. Servicios del frontend

```
Siguiendo las reglas de React, crea:
- src/services/apiClient.js: instancia de Axios con baseURL desde VITE_API_URL y un interceptor que convierta los errores en un Error con el message del backend o un mensaje de conexión.
- accountService.js: getAll(), getByNumber(number), create(account)
- transferService.js: transfer(transferRequest)
- transactionService.js: getAll(accountNumber?), getByReference(reference)
- src/utils/format.js: formatCurrency y formatDateTime
Crea .env con VITE_API_URL=http://localhost:8080/api y .env.example, y agrega .env al .gitignore.
```

## 10. AccountList

```
Crea AccountList (hook useAccounts.js, AccountList.jsx y AccountList.css): muestra las cuentas en tarjetas con el número, el titular y el saldo en COP. Expone una función refresh() para recargar después de cada transferencia. Incluye estados de carga, error y lista vacía.
```

## 11. TransferForm, Toast y TransactionReceipt

```
Crea TransferForm (useTransferForm.js, TransferForm.jsx y TransferForm.css):
- Campos: cuenta origen y cuenta destino (selects cargados desde accountService, mostrando número, titular y saldo), monto y descripción (opcional).
- Validaciones: campos obligatorios, monto mayor que 0 y como máximo el saldo del origen, origen distinto de destino. Un mensaje de error por campo.
- Al enviar, llama a transferService.transfer; si sale bien, refresca las cuentas y el historial y muestra el comprobante.
- Si hay error, muestra el mensaje del backend en un Toast propio (src/components/Toast + useToast.js) que se cierre a los 4 segundos.

Crea TransactionReceipt (componente con su .css): muestra la referencia, el origen y el destino (número y titular), el monto en COP, la fecha y el estado, y un código QR (QRCodeSVG de qrcode.react) que codifique en JSON: reference, sourceAccountNumber, targetAccountNumber, amount y createdAt. Incluye un botón "Descargar QR" que lo guarde como imagen PNG y un botón "Cerrar".
```

## 12. Ejecución

```
ejecuta lo actual
calve de base de datos es 1204
```

## 13. TransactionHistory y App

```
Crea TransactionHistory (useTransactionHistory.js, TransactionHistory.jsx y TransactionHistory.css):
- Tabla con fecha, referencia (abreviada), origen, destino, monto en COP, descripción y estado.
- Filtro por cuenta (select con "Todas" y cada cuenta) que llama a transactionService.getAll(accountNumber).
- Botón "Ver QR" en cada fila que abre el TransactionReceipt de esa transacción.
- Estados de carga, error con "Reintentar" y lista vacía. Se refresca después de cada transferencia.

Organiza App.jsx en tres secciones: Cuentas, Nueva transferencia e Historial, con un encabezado con el nombre del banco. Termina con npm run lint y npm run build, y dime cómo levantar todo.
```

## 14. README, prompts y Git

```
Genera un README.md completo y detallado en la raíz con: descripción del sistema, stack tecnológico del backend y del frontend con sus versiones (léelas de build.gradle y package.json), arquitectura (capas, estructura de carpetas y flujo de una transferencia), modelo de datos (Account y Transaction), endpoints con ejemplos de request y response, reglas de negocio, cómo funciona el QR y qué datos codifica, requisitos previos, configuración de la base de datos con los perfiles default y local, cómo ejecutar el back y el front, y una sección de capturas.
Copia este archivo de prompts a docs/prompts.md.
Inicializa git, revisa que el .gitignore excluya build, node_modules, .env y application-local.properties, haz el commit y dame los comandos para subirlo a GitHub.
```
