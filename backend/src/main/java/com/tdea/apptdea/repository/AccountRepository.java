package com.tdea.apptdea.repository;

import com.tdea.apptdea.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Acceso a datos de las cuentas bancarias.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Busca una cuenta por su número.
     *
     * @param accountNumber número de cuenta de 10 dígitos
     * @return la cuenta, o vacío si no existe
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * Indica si ya existe una cuenta con el número dado.
     *
     * @param accountNumber número de cuenta de 10 dígitos
     * @return {@code true} si existe, {@code false} en caso contrario
     */
    boolean existsByAccountNumber(String accountNumber);
}
