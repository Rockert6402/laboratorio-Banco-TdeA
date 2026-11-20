package com.tdea.apptdea.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Datos de salida de una cuenta.
 *
 * @param accountNumber número de cuenta de 10 dígitos
 * @param ownerName     nombre del titular
 * @param balance       saldo actual
 * @param createdAt     fecha de creación
 */
public record AccountResponse(
        String accountNumber,
        String ownerName,
        BigDecimal balance,
        LocalDateTime createdAt
) {
}
