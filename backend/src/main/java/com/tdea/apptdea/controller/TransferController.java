package com.tdea.apptdea.controller;

import com.tdea.apptdea.dto.ErrorResponse;
import com.tdea.apptdea.dto.TransactionResponse;
import com.tdea.apptdea.dto.TransferRequest;
import com.tdea.apptdea.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Endpoint REST para ejecutar transferencias entre cuentas.
 */
@RestController
@RequestMapping("/api/transfers")
@Tag(name = "Transferencias", description = "Transferencias de dinero entre cuentas")
public class TransferController {

    private final TransferService transferService;

    /**
     * Crea el controlador de transferencias.
     *
     * @param transferService servicio de transferencias
     */
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    /**
     * Ejecuta una transferencia.
     *
     * @param request datos de la transferencia
     * @return 201 con la transacción registrada y la cabecera {@code Location}
     *         apuntando a {@code /api/transactions/{reference}}
     */
    @PostMapping
    @Operation(summary = "Realizar una transferencia",
            description = "Debita el origen, acredita el destino y registra la transacción de forma atómica.")
    @ApiResponse(responseCode = "201", description = "Transferencia completada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "La cuenta de origen o de destino no existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Saldo insuficiente, misma cuenta o conflicto de concurrencia",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request) {
        TransactionResponse transaction = transferService.transfer(request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/transactions/{reference}")
                .buildAndExpand(transaction.reference())
                .toUri();
        return ResponseEntity.created(location).body(transaction);
    }
}
