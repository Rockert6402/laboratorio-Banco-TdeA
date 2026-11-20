package com.tdea.apptdea.service;

import com.tdea.apptdea.dto.AccountRequest;
import com.tdea.apptdea.dto.AccountResponse;
import com.tdea.apptdea.entity.Account;
import com.tdea.apptdea.exception.BusinessRuleException;
import com.tdea.apptdea.exception.ResourceNotFoundException;
import com.tdea.apptdea.mapper.AccountMapper;
import com.tdea.apptdea.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Casos de uso de las cuentas bancarias: consulta y creación.
 */
@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    /**
     * Crea el servicio de cuentas.
     *
     * @param accountRepository repositorio de cuentas
     * @param accountMapper     conversor entidad/DTO
     */
    public AccountService(AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    /**
     * Devuelve todas las cuentas ordenadas por número de cuenta.
     *
     * @return lista de cuentas; vacía si no hay ninguna
     */
    @Transactional(readOnly = true)
    public List<AccountResponse> findAll() {
        return accountRepository.findAll(Sort.by("accountNumber")).stream()
                .map(accountMapper::toResponse)
                .toList();
    }

    /**
     * Busca una cuenta por su número.
     *
     * @param number número de cuenta de 10 dígitos
     * @return la cuenta encontrada
     * @throws ResourceNotFoundException si la cuenta no existe
     */
    @Transactional(readOnly = true)
    public AccountResponse findByNumber(String number) {
        return accountRepository.findByAccountNumber(number)
                .map(accountMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("La cuenta " + number + " no existe"));
    }

    /**
     * Crea una cuenta nueva.
     *
     * @param request datos de la cuenta
     * @return la cuenta creada
     * @throws BusinessRuleException si el número ya existe o el saldo inicial es negativo
     */
    @Transactional
    public AccountResponse create(AccountRequest request) {
        if (request.initialBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException("El saldo inicial no puede ser negativo");
        }
        if (accountRepository.existsByAccountNumber(request.accountNumber())) {
            throw new BusinessRuleException("Ya existe una cuenta con el número " + request.accountNumber());
        }

        Account saved = accountRepository.save(accountMapper.toEntity(request));
        log.info("Cuenta creada con id {}", saved.getId());
        return accountMapper.toResponse(saved);
    }
}
