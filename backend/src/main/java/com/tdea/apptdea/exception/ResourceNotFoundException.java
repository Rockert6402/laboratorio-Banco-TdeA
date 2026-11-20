package com.tdea.apptdea.exception;

/**
 * Se lanza cuando el recurso solicitado no existe. Se responde con 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Crea la excepción con un mensaje para el cliente.
     *
     * @param message descripción del recurso que no se encontró
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
