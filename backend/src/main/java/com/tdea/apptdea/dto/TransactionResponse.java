package com.tdea.apptdea.dto;

import com.tdea.apptdea.entity.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Datos de salida de una transacción.
 *
 * @param reference           referencia única (UUID)
 * @param sourceAccountNumber número de la cuenta de origen
 * @param sourceOwnerName     titular de la cuenta de origen
 * @param targetAccountNumber número de la cuenta de destino
 * @param targetOwnerName     titular de la cuenta de destino
 * @param amount              monto transferido
 * @param description         descripción, o {@code null}
 * @param createdAt           fecha de registro
 * @param status              estado final
 */
public record TransactionResponse(
        String reference,
        String sourceAccountNumber,
        String sourceOwnerName,
        String targetAccountNumber,
        String targetOwnerName,
        BigDecimal amount,
        String description,
        LocalDateTime createdAt,
        TransactionStatus status
) {
}
