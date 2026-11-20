package com.tdea.apptdea.exception;

/**
 * Se lanza cuando una operación viola una regla de negocio
 * (por ejemplo, transferir a la misma cuenta). Se responde con 409.
 */
public class BusinessRuleException extends RuntimeException {

    /**
     * Crea la excepción con un mensaje para el cliente.
     *
     * @param message descripción de la regla violada
     */
    public BusinessRuleException(String message) {
        super(message);
    }
}
