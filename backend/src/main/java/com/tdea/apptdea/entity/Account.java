package com.tdea.apptdea.entity;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Cuenta bancaria con número único de 10 dígitos, titular y saldo.
 * <p>
 * Usa bloqueo optimista mediante {@link Version} para evitar actualizaciones
 * concurrentes inconsistentes del saldo.
 */
@Entity
@Table(name = "accounts",
        check = @CheckConstraint(name = "ck_accounts_account_number",
                constraint = "account_number ~ '^[0-9]{10}$'"))
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true, length = 10)
    private String accountNumber;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    /**
     * Constructor requerido por JPA. No usar desde el código de negocio.
     */
    protected Account() {
    }

    /**
     * Crea una cuenta nueva.
     *
     * @param accountNumber número de cuenta de 10 dígitos
     * @param ownerName     nombre del titular
     * @param balance       saldo inicial
     */
    public Account(String accountNumber, String ownerName, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }

    /**
     * Asigna la fecha de creación antes de insertar la cuenta, si no se asignó antes.
     */
    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    /**
     * Devuelve el identificador de la cuenta.
     *
     * @return identificador generado por la base de datos, o {@code null} si aún no se persiste
     */
    public Long getId() {
        return id;
    }

    /**
     * Devuelve el número de cuenta.
     *
     * @return número de cuenta de 10 dígitos
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Cambia el número de cuenta.
     *
     * @param accountNumber nuevo número de cuenta de 10 dígitos
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Devuelve el nombre del titular.
     *
     * @return nombre del titular
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * Cambia el nombre del titular.
     *
     * @param ownerName nuevo nombre del titular
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * Devuelve el saldo actual.
     *
     * @return saldo con escala 2
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Cambia el saldo de la cuenta.
     *
     * @param balance nuevo saldo
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    /**
     * Devuelve la fecha de creación de la cuenta.
     *
     * @return fecha y hora de creación
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Devuelve la versión usada para el bloqueo optimista.
     *
     * @return versión actual de la fila
     */
    public Long getVersion() {
        return version;
    }
}
