package com.tdea.apptdea.service;

import com.tdea.apptdea.dto.TransactionResponse;
import com.tdea.apptdea.dto.TransferRequest;
import com.tdea.apptdea.entity.Account;
import com.tdea.apptdea.entity.Transaction;
import com.tdea.apptdea.entity.TransactionStatus;
import com.tdea.apptdea.exception.BusinessRuleException;
import com.tdea.apptdea.exception.InsufficientFundsException;
import com.tdea.apptdea.exception.ResourceNotFoundException;
import com.tdea.apptdea.mapper.TransactionMapper;
import com.tdea.apptdea.repository.AccountRepository;
import com.tdea.apptdea.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Ejecuta transferencias entre cuentas de forma atómica.
 */
@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    /**
     * Crea el servicio de transferencias.
     *
     * @param accountRepository     repositorio de cuentas
     * @param transactionRepository repositorio de transacciones
     * @param transactionMapper     conversor entidad/DTO de transacciones
     */
    public TransferService(AccountRepository accountRepository,
                           TransactionRepository transactionRepository,
                           TransactionMapper transactionMapper) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    /**
     * Transfiere dinero de una cuenta a otra.
     * <p>
     * El débito, el crédito y el registro de la transacción ocurren en una sola
     * transacción de base de datos: si algo falla, no se guarda nada. Si otra
     * operación modificó una de las cuentas al mismo tiempo, el bloqueo
     * optimista ({@code @Version}) aborta la transferencia al confirmar.
     *
     * @param request datos de la transferencia
     * @return la transacción registrada con estado {@link TransactionStatus#COMPLETED}
     * @throws BusinessRuleException      si el monto no es mayor que 0 o las cuentas son la misma
     * @throws ResourceNotFoundException  si la cuenta de origen o la de destino no existe
     * @throws InsufficientFundsException si el origen no tiene saldo suficiente
     */
    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        BigDecimal amount = request.amount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("El monto debe ser mayor que 0");
        }

        Account source = findAccount(request.sourceAccountNumber(), "origen");
        Account target = findAccount(request.targetAccountNumber(), "destino");

        if (source.getId().equals(target.getId())) {
            throw new BusinessRuleException("La cuenta de origen y la de destino deben ser distintas");
        }
        if (source.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }

        source.setBalance(source.getBalance().subtract(amount));
        target.setBalance(target.getBalance().add(amount));

        Transaction saved = transactionRepository.save(
                new Transaction(source, target, amount, request.description(), TransactionStatus.COMPLETED));

        log.info("Transferencia {} completada", saved.getReference());
        return transactionMapper.toResponse(saved);
    }

    /**
     * Busca una cuenta por número o lanza 404 indicando su rol en la transferencia.
     */
    private Account findAccount(String accountNumber, String role) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La cuenta de " + role + " " + accountNumber + " no existe"));
    }
}
