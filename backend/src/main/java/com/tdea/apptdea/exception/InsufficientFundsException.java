package com.tdea.apptdea.exception;

/**
 * Se lanza cuando la cuenta de origen no tiene saldo suficiente para una
 * transferencia. Se responde con 409.
 */
public class InsufficientFundsException extends BusinessRuleException {

    /**
     * Crea la excepción con el mensaje estándar de saldo insuficiente.
     */
    public InsufficientFundsException() {
        super("Saldo insuficiente en la cuenta de origen");
    }
}
