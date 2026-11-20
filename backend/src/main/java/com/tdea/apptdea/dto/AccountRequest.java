package com.tdea.apptdea.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Datos de entrada para crear una cuenta.
 *
 * @param accountNumber  número de cuenta de exactamente 10 dígitos
 * @param ownerName      nombre del titular
 * @param initialBalance saldo inicial, no negativo y con máximo 2 decimales
 */
public record AccountRequest(
        @NotBlank(message = "El número de cuenta es obligatorio")
        @Pattern(regexp = "\\d{10}", message = "El número de cuenta debe tener exactamente 10 dígitos")
        String accountNumber,

        @NotBlank(message = "El nombre del titular es obligatorio")
        @Size(max = 100, message = "El nombre del titular no puede superar 100 caracteres")
        String ownerName,

        @NotNull(message = "El saldo inicial es obligatorio")
        @PositiveOrZero(message = "El saldo inicial no puede ser negativo")
        @Digits(integer = 13, fraction = 2, message = "El saldo inicial admite máximo 13 enteros y 2 decimales")
        BigDecimal initialBalance
) {
}
