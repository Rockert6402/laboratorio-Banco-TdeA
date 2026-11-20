package com.tdea.apptdea.repository;

import com.tdea.apptdea.entity.Transaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a datos de las transacciones.
 * <p>
 * Las consultas cargan las cuentas de origen y destino en la misma sentencia
 * para evitar el problema N+1 al convertir las transacciones en DTO, ya que las
 * relaciones son LAZY.
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Devuelve las transacciones en las que la cuenta participa como origen o
     * como destino, de la más reciente a la más antigua.
     *
     * @param accountNumber número de cuenta de 10 dígitos
     * @return transacciones de la cuenta; lista vacía si no tiene ninguna
     */
    @Query("""
            SELECT t FROM Transaction t
            JOIN FETCH t.sourceAccount s
            JOIN FETCH t.targetAccount d
            WHERE s.accountNumber = :accountNumber
               OR d.accountNumber = :accountNumber
            ORDER BY t.createdAt DESC
            """)
    List<Transaction> findByAccountNumber(@Param("accountNumber") String accountNumber);

    /**
     * Devuelve todas las transacciones, de la más reciente a la más antigua.
     *
     * @return todas las transacciones; lista vacía si no hay ninguna
     */
    @EntityGraph(attributePaths = {"sourceAccount", "targetAccount"})
    List<Transaction> findAllByOrderByCreatedAtDesc();

    /**
     * Busca una transacción por su referencia única.
     *
     * @param reference UUID de la transacción en formato texto
     * @return la transacción, o vacío si no existe
     */
    @EntityGraph(attributePaths = {"sourceAccount", "targetAccount"})
    Optional<Transaction> findByReference(String reference);
}
