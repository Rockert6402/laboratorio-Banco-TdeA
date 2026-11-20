package com.tdea.apptdea.dto;

import java.time.Instant;

/**
 * Cuerpo de respuesta para cualquier error de la API.
 *
 * @param message   mensaje legible para el cliente, sin detalles internos
 * @param timestamp momento en que ocurrió el error
 */
public record ErrorResponse(String message, Instant timestamp) {

    /**
     * Crea una respuesta de error con la hora actual.
     *
     * @param message mensaje para el cliente
     * @return respuesta de error
     */
    public static ErrorResponse of(String message) {
        return new ErrorResponse(message, Instant.now());
    }
}
