package com.tdea.apptdea.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Registro de una transferencia entre dos cuentas.
 * <p>
 * Es inmutable: una vez guardada no se modifica ni se borra. Por eso no tiene
 * setters y todos sus valores se fijan en el constructor. Una corrección se
 * registra como una transacción nueva.
 */
@Entity
@Immutable
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference", nullable = false, unique = true, length = 36, updatable = false)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_account_id", nullable = false, updatable = false)
    private Account sourceAccount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_account_id", nullable = false, updatable = false)
    private Account targetAccount;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2, updatable = false)
    private BigDecimal amount;

    @Column(name = "description", updatable = false)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20, updatable = false)
    private TransactionStatus status;

    /**
     * Constructor requerido por JPA. No usar desde el código de negocio.
     */
    protected Transaction() {
    }

    /**
     * Crea el registro de una transferencia con una referencia UUID nueva.
     *
     * @param sourceAccount cuenta de origen
     * @param targetAccount cuenta de destino
     * @param amount        monto transferido
     * @param description   descripción opcional; puede ser {@code null}
     * @param status        estado final de la transferencia
     */
    public Transaction(Account sourceAccount, Account targetAccount, BigDecimal amount,
                       String description, TransactionStatus status) {
        this.reference = UUID.randomUUID().toString();
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
        this.amount = amount;
        this.description = description;
        this.status = status;
    }

    /**
     * Asigna la fecha de creación antes de insertar la transacción.
     */
    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    /**
     * Devuelve el identificador de la transacción.
     *
     * @return identificador generado, o {@code null} si aún no se persiste
     */
    public Long getId() {
        return id;
    }

    /**
     * Devuelve la referencia única de la transacción.
     *
     * @return UUID en formato texto
     */
    public String getReference() {
        return reference;
    }

    /**
     * Devuelve la cuenta de origen (carga perezosa).
     *
     * @return cuenta debitada
     */
    public Account getSourceAccount() {
        return sourceAccount;
    }

    /**
     * Devuelve la cuenta de destino (carga perezosa).
     *
     * @return cuenta acreditada
     */
    public Account getTargetAccount() {
        return targetAccount;
    }

    /**
     * Devuelve el monto transferido.
     *
     * @return monto con escala 2
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Devuelve la descripción de la transacción.
     *
     * @return descripción, o {@code null} si no se indicó
     */
    public String getDescription() {
        return description;
    }

    /**
     * Devuelve la fecha de creación de la transacción.
     *
     * @return fecha y hora de registro
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Devuelve el estado final de la transacción.
     *
     * @return {@link TransactionStatus#COMPLETED} o {@link TransactionStatus#FAILED}
     */
    public TransactionStatus getStatus() {
        return status;
    }
}
