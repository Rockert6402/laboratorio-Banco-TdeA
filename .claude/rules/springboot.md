# Reglas del backend: servicio bancario (Java 25 + Spring Boot 4)

Estas reglas aplican **siempre** a todo el código del backend (`backend/`). Si una petición entra en conflicto con ellas, señálalo antes de escribir código.

## Nombres e idioma

- Clases, records, enums, métodos, atributos y variables en **inglés**. Comentarios y Javadoc en **español**.
- Sufijos por capa: `*Controller`, `*Service`, `*Repository`, `*Mapper`, `*Exception`. DTO: `*Request` (entrada) y `*Response` (salida).
- Tablas y columnas en inglés con `snake_case` (`account_number`, `created_at`).
- Endpoints bajo `/api/<recurso>` en plural y minúsculas (`/api/accounts`, `/api/transfers`).

## Documentación

- Toda clase pública y todo método público llevan **Javadoc** con descripción y, según aplique, `@param`, `@return` y `@throws`.
- El Javadoc se actualiza en el mismo cambio que modifica la firma o el comportamiento.

## Arquitectura

- Flujo obligatorio: **Controller → Service → Repository**. Un controlador nunca llama a un repositorio.
- Los controladores no contienen lógica de negocio: reciben, validan (`@Valid`), delegan al servicio y arman la respuesta HTTP.
- `@Transactional` va en los servicios; las lecturas usan `@Transactional(readOnly = true)`.
- La conversión entre entidad y DTO se hace en un `*Mapper` dedicado, nunca en el controlador ni dentro de la entidad.

## Dinero y transacciones (reglas críticas)

- Montos y saldos **siempre** con `BigDecimal`, mapeados con `@Column(precision = 15, scale = 2)`. **Prohibido** `double`, `float` o sus wrappers para dinero.
- Crear `BigDecimal` desde `String` o con `BigDecimal.valueOf(...)`, nunca con `new BigDecimal(double)`.
- Comparar montos con `compareTo`, **nunca** con `equals` (`2.0` y `2.00` no son `equals`).
- Una transferencia es **atómica**: el débito, el crédito y el registro de la transacción ocurren dentro del mismo método `@Transactional`; si algo falla, no se guarda nada.
- Control de concurrencia en las cuentas con `@Version` (bloqueo optimista). El conflicto de versión se traduce a 409.
- Las transacciones registradas **nunca se modifican ni se borran**: no se exponen endpoints `PUT`/`PATCH`/`DELETE` sobre ellas ni métodos de servicio que lo hagan. Una corrección se registra como una nueva transacción.

## DTO y API

- Los DTO de entrada y salida son **Java records** con Bean Validation (`@NotNull`, `@NotBlank`, `@Positive`, `@Digits(integer = 13, fraction = 2)`, etc.). Requiere la dependencia `spring-boot-starter-validation`.
- **Nunca** exponer entidades JPA en la API, ni como entrada ni como salida.
- Códigos HTTP:
  - `GET` → 200.
  - `POST` → 201 con cabecera `Location` apuntando al recurso creado.
  - Error de validación → 400.
  - Recurso inexistente → 404.
  - Violación de regla de negocio (saldo insuficiente, misma cuenta origen y destino, cuenta inactiva, conflicto de concurrencia) → 409.

## Errores

- Un único `GlobalExceptionHandler` anotado con `@RestControllerAdvice` traduce las excepciones a respuestas HTTP.
- El cuerpo de error es el record `ErrorResponse(String message, Instant timestamp)`.
- Excepciones de dominio propias con sufijo `*Exception` (p. ej. `AccountNotFoundException`, `InsufficientFundsException`).
- Sin `try/catch` en los controladores. Nunca se envían stack traces ni mensajes internos al cliente; el detalle técnico va al log.

## Java 25 y Spring

- Inyección **por constructor** con campos `private final`. **Prohibido** `@Autowired` en campos.
- Usar records, `switch` con patrones, `Stream.toList()`.
- `var` solo cuando el tipo es evidente en la misma línea.
- `Optional` solo como tipo de retorno (no en campos ni parámetros). Usar `orElseThrow(...)`; **nunca** `get()` sin verificar.
- Entidades JPA:
  - Sin `@Data` (ni Lombok que genere `equals`/`hashCode` sobre todos los campos).
  - Relaciones `FetchType.LAZY`.
  - Enums con `@Enumerated(EnumType.STRING)`.
  - Fechas con `java.time` (`Instant`, `LocalDate`, `LocalDateTime`); prohibido `java.util.Date`.
- Logs con SLF4J (`LoggerFactory.getLogger(...)`); prohibido `System.out.println`. No registrar datos sensibles completos (credenciales, números de cuenta completos).

## Seguridad y configuración

- Credenciales en variables de entorno con valores por defecto para desarrollo, p. ej.:
  ```properties
  spring.datasource.username=${DB_USER:postgres}
  spring.datasource.password=${DB_PASSWORD:root}
  ```
- Los archivos con credenciales personales (`.env`, `application-local.properties`, etc.) se agregan al `.gitignore` y **no se suben** al repositorio.

## Estructura actual del proyecto

Proyecto Gradle `apptdea` (grupo `com.tdea`), Spring Boot 4.1.2, Java 25, PostgreSQL, Spring Data JPA, Spring Web MVC, Bean Validation y springdoc-openapi.

```
backend/
├── CLAUDE.md
├── build.gradle
├── settings.gradle
├── gradlew / gradlew.bat
├── gradle/wrapper/
└── src/
    ├── main/
    │   ├── java/com/tdea/apptdea/
    │   │   ├── ApptdeaApplication.java
    │   │   ├── config/
    │   │   │   ├── CorsConfig.java            # CORS (CORS_ALLOWED_ORIGINS, por defecto http://localhost:3000)
    │   │   │   ├── OpenApiConfig.java         # título y descripción de Swagger
    │   │   │   └── DataSeeder.java            # 4 cuentas de prueba si accounts está vacía
    │   │   ├── controller/
    │   │   │   ├── AccountController.java     # /api/accounts
    │   │   │   ├── TransactionController.java # /api/transactions
    │   │   │   └── TransferController.java    # /api/transfers
    │   │   ├── dto/
    │   │   │   ├── AccountRequest.java
    │   │   │   ├── AccountResponse.java
    │   │   │   ├── ErrorResponse.java
    │   │   │   ├── TransactionResponse.java
    │   │   │   └── TransferRequest.java
    │   │   ├── entity/
    │   │   │   ├── Account.java               # tabla accounts, @Version
    │   │   │   ├── Transaction.java           # tabla transactions, @Immutable
    │   │   │   └── TransactionStatus.java     # COMPLETED, FAILED
    │   │   ├── exception/
    │   │   │   ├── BusinessRuleException.java       # 409
    │   │   │   ├── GlobalExceptionHandler.java      # 400 / 404 / 409 / 500
    │   │   │   ├── InsufficientFundsException.java  # 409, extiende BusinessRuleException
    │   │   │   └── ResourceNotFoundException.java   # 404
    │   │   ├── mapper/
    │   │   │   ├── AccountMapper.java
    │   │   │   └── TransactionMapper.java
    │   │   ├── repository/
    │   │   │   ├── AccountRepository.java
    │   │   │   └── TransactionRepository.java
    │   │   └── service/
    │   │       ├── AccountService.java
    │   │       ├── TransactionService.java
    │   │       └── TransferService.java
    │   └── resources/
    │       ├── application.properties                 # perfil default (PostgreSQL banktdea)
    │       ├── application-local.properties           # perfil local, ignorado por git
    │       ├── application-local.properties.example   # plantilla versionada del perfil local
    │       ├── static/
    │       └── templates/
    └── test/
        └── java/com/tdea/apptdea/
            └── ApptdeaApplicationTests.java
```

**Regla de mantenimiento:** cada vez que se cree, mueva, renombre o elimine un archivo o paquete del backend, actualiza esta sección en el mismo cambio para que refleje la estructura real del disco.
