package com.tdea.apptdea.entity;

/**
 * Estado final de una transacción registrada.
 */
public enum TransactionStatus {

    /** La transferencia se aplicó correctamente. */
    COMPLETED,

    /** La transferencia no se aplicó. */
    FAILED
}
