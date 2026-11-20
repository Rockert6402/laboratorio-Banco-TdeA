package com.tdea.apptdea.mapper;

import com.tdea.apptdea.dto.AccountRequest;
import com.tdea.apptdea.dto.AccountResponse;
import com.tdea.apptdea.entity.Account;
import org.springframework.stereotype.Component;

/**
 * Convierte entre la entidad {@link Account} y sus DTO.
 */
@Component
public class AccountMapper {

    /**
     * Crea una entidad nueva a partir de la solicitud.
     *
     * @param request datos validados de la cuenta
     * @return entidad sin persistir
     */
    public Account toEntity(AccountRequest request) {
        return new Account(request.accountNumber(), request.ownerName().strip(), request.initialBalance());
    }

    /**
     * Convierte la entidad en su DTO de salida.
     *
     * @param account entidad de la cuenta
     * @return DTO de la cuenta
     */
    public AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getAccountNumber(),
                account.getOwnerName(),
                account.getBalance(),
                account.getCreatedAt()
        );
    }
}
