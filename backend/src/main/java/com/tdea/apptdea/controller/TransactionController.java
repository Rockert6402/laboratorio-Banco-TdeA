package com.tdea.apptdea.controller;

import com.tdea.apptdea.dto.ErrorResponse;
import com.tdea.apptdea.dto.TransactionResponse;
import com.tdea.apptdea.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints REST de consulta de transacciones. Solo lectura.
 */
@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transacciones", description = "Historial de transacciones registradas")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Crea el controlador de transacciones.
     *
     * @param transactionService servicio de consulta de transacciones
     */
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Lista las transacciones, de la más reciente a la más antigua. Si se indica
     * {@code accountNumber}, solo las que tienen esa cuenta como origen o destino.
     *
     * @param accountNumber número de cuenta opcional para filtrar
     * @return 200 con la lista de transacciones
     */
    @GetMapping
    @Operation(summary = "Listar transacciones",
            description = "Más recientes primero. Con accountNumber filtra por cuenta de origen o destino.")
    @ApiResponse(responseCode = "200", description = "Lista de transacciones")
    @ApiResponse(responseCode = "400", description = "Número de cuenta con formato inválido",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "La cuenta indicada no existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public List<TransactionResponse> findAll(
            @Parameter(description = "Número de cuenta de 10 dígitos (opcional)")
            @RequestParam(required = false)
            @Pattern(regexp = "\\d{10}", message = "El número de cuenta debe tener exactamente 10 dígitos")
            String accountNumber) {
        return accountNumber == null
                ? transactionService.findAll()
                : transactionService.findByAccount(accountNumber);
    }

    /**
     * Consulta una transacción por su referencia.
     *
     * @param reference UUID de la transacción
     * @return 200 con la transacción
     */
    @GetMapping("/{reference}")
    @Operation(summary = "Consultar una transacción por referencia")
    @ApiResponse(responseCode = "200", description = "Transacción encontrada")
    @ApiResponse(responseCode = "404", description = "La transacción no existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public TransactionResponse findByReference(@PathVariable String reference) {
        return transactionService.findByReference(reference);
    }
}
