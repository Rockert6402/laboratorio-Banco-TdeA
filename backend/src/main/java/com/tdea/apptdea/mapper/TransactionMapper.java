package com.tdea.apptdea.mapper;

import com.tdea.apptdea.dto.TransactionResponse;
import com.tdea.apptdea.entity.Account;
import com.tdea.apptdea.entity.Transaction;
import org.springframework.stereotype.Component;

/**
 * Convierte la entidad {@link Transaction} en su DTO de salida.
 */
@Component
public class TransactionMapper {

    /**
     * Convierte la transacción en su DTO de salida. Las cuentas de origen y
     * destino deben estar cargadas o la sesión JPA abierta.
     *
     * @param transaction entidad de la transacción
     * @return DTO de la transacción
     */
    public TransactionResponse toResponse(Transaction transaction) {
        Account source = transaction.getSourceAccount();
        Account target = transaction.getTargetAccount();
        return new TransactionResponse(
                transaction.getReference(),
                source.getAccountNumber(),
                source.getOwnerName(),
                target.getAccountNumber(),
                target.getOwnerName(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getCreatedAt(),
                transaction.getStatus()
        );
    }
}
