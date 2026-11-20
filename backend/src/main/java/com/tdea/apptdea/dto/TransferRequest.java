package com.tdea.apptdea.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Datos de entrada para una transferencia entre cuentas.
 *
 * @param sourceAccountNumber número de la cuenta de origen (10 dígitos)
 * @param targetAccountNumber número de la cuenta de destino (10 dígitos)
 * @param amount              monto a transferir, mayor que 0 y con máximo 2 decimales
 * @param description         descripción opcional
 */
public record TransferRequest(
        @NotBlank(message = "La cuenta de origen es obligatoria")
        @Pattern(regexp = "\\d{10}", message = "La cuenta de origen debe tener exactamente 10 dígitos")
        String sourceAccountNumber,

        @NotBlank(message = "La cuenta de destino es obligatoria")
        @Pattern(regexp = "\\d{10}", message = "La cuenta de destino debe tener exactamente 10 dígitos")
        String targetAccountNumber,

        @NotNull(message = "El monto es obligatorio")
        @Positive(message = "El monto debe ser mayor que 0")
        @Digits(integer = 13, fraction = 2, message = "El monto admite máximo 13 enteros y 2 decimales")
        BigDecimal amount,

        @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
        String description
) {
}
