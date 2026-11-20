package com.tdea.apptdea.exception;

import com.tdea.apptdea.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * Traduce las excepciones de la aplicación a respuestas HTTP con cuerpo
 * {@link ErrorResponse}. Nunca devuelve stack traces ni detalles internos.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Errores de Bean Validation en el cuerpo de la petición.
     *
     * @param ex excepción con los campos inválidos
     * @return 400 con los mensajes de validación
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Errores de validación en parámetros de ruta o de consulta.
     *
     * @param ex excepción de validación del método
     * @return 400 con los mensajes de validación
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleMethodValidation(HandlerMethodValidationException ex) {
        String message = ex.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Cuerpo JSON mal formado o con tipos incorrectos.
     *
     * @param ex excepción de lectura del cuerpo
     * @return 400
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no es válido");
    }

    /**
     * Parámetro de ruta o de consulta con un tipo incorrecto.
     *
     * @param ex excepción de conversión de tipo
     * @return 400
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST, "El parámetro '" + ex.getName() + "' no es válido");
    }

    /**
     * Recurso de dominio inexistente.
     *
     * @param ex excepción con el recurso no encontrado
     * @return 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Ruta inexistente.
     *
     * @param ex excepción de recurso estático no encontrado
     * @return 404
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Recurso no encontrado");
    }

    /**
     * Violación de una regla de negocio, incluido el saldo insuficiente.
     *
     * @param ex excepción de regla de negocio
     * @return 409
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(BusinessRuleException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Conflicto de concurrencia detectado por {@code @Version}.
     *
     * @param ex excepción de bloqueo optimista
     * @return 409
     */
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock(OptimisticLockingFailureException ex) {
        log.warn("Conflicto de concurrencia: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT,
                "La cuenta fue modificada por otra operación al mismo tiempo; intenta de nuevo");
    }

    /**
     * Violación de una restricción de la base de datos (por ejemplo, número de
     * cuenta duplicado creado en paralelo).
     *
     * @param ex excepción de integridad de datos
     * @return 409
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Violación de integridad de datos: {}", ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT, "La operación viola una restricción de datos");
    }

    /**
     * Cualquier error no previsto. El detalle queda en el log, no en la respuesta.
     *
     * @param ex excepción inesperada
     * @return 500 con un mensaje genérico
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Error inesperado", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno");
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(ErrorResponse.of(message));
    }
}
