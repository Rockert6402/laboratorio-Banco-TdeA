package com.tdea.apptdea.service;

import com.tdea.apptdea.dto.TransactionResponse;
import com.tdea.apptdea.exception.ResourceNotFoundException;
import com.tdea.apptdea.mapper.TransactionMapper;
import com.tdea.apptdea.repository.AccountRepository;
import com.tdea.apptdea.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Consulta de transacciones registradas. Solo lectura: las transacciones no se
 * modifican ni se borran.
 */
@Service
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    /**
     * Crea el servicio de consulta de transacciones.
     *
     * @param transactionRepository repositorio de transacciones
     * @param accountRepository     repositorio de cuentas
     * @param transactionMapper     conversor entidad/DTO
     */
    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.transactionMapper = transactionMapper;
    }

    /**
     * Devuelve todas las transacciones, de la más reciente a la más antigua.
     *
     * @return lista de transacciones; vacía si no hay ninguna
     */
    public List<TransactionResponse> findAll() {
        return transactionRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    /**
     * Devuelve las transacciones en las que la cuenta es origen o destino,
     * de la más reciente a la más antigua.
     *
     * @param number número de cuenta de 10 dígitos
     * @return transacciones de la cuenta; vacía si no tiene ninguna
     * @throws ResourceNotFoundException si la cuenta no existe
     */
    public List<TransactionResponse> findByAccount(String number) {
        if (!accountRepository.existsByAccountNumber(number)) {
            throw new ResourceNotFoundException("La cuenta " + number + " no existe");
        }
        return transactionRepository.findByAccountNumber(number).stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    /**
     * Busca una transacción por su referencia.
     *
     * @param reference UUID de la transacción
     * @return la transacción encontrada
     * @throws ResourceNotFoundException si no existe una transacción con esa referencia
     */
    public TransactionResponse findByReference(String reference) {
        return transactionRepository.findByReference(reference)
                .map(transactionMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La transacción " + reference + " no existe"));
    }
}
