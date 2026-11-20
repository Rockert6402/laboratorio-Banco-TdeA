package com.tdea.apptdea.controller;

import com.tdea.apptdea.dto.AccountRequest;
import com.tdea.apptdea.dto.AccountResponse;
import com.tdea.apptdea.dto.ErrorResponse;
import com.tdea.apptdea.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Endpoints REST de las cuentas bancarias.
 */
@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Cuentas", description = "Consulta y creación de cuentas bancarias")
public class AccountController {

    private final AccountService accountService;

    /**
     * Crea el controlador de cuentas.
     *
     * @param accountService servicio de cuentas
     */
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Lista todas las cuentas.
     *
     * @return 200 con la lista de cuentas
     */
    @GetMapping
    @Operation(summary = "Listar cuentas")
    @ApiResponse(responseCode = "200", description = "Lista de cuentas")
    public List<AccountResponse> findAll() {
        return accountService.findAll();
    }

    /**
     * Consulta una cuenta por su número.
     *
     * @param number número de cuenta de 10 dígitos
     * @return 200 con la cuenta
     */
    @GetMapping("/{number}")
    @Operation(summary = "Consultar una cuenta por número")
    @ApiResponse(responseCode = "200", description = "Cuenta encontrada")
    @ApiResponse(responseCode = "400", description = "Número con formato inválido",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "La cuenta no existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public AccountResponse findByNumber(
            @PathVariable
            @Pattern(regexp = "\\d{10}", message = "El número de cuenta debe tener exactamente 10 dígitos")
            String number) {
        return accountService.findByNumber(number);
    }

    /**
     * Crea una cuenta nueva.
     *
     * @param request datos de la cuenta
     * @return 201 con la cuenta creada y la cabecera {@code Location}
     */
    @PostMapping
    @Operation(summary = "Crear una cuenta")
    @ApiResponse(responseCode = "201", description = "Cuenta creada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "El número de cuenta ya existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountRequest request) {
        AccountResponse created = accountService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{number}")
                .buildAndExpand(created.accountNumber())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
}
