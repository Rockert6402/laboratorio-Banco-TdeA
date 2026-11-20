package com.tdea.apptdea.config;

import com.tdea.apptdea.entity.Account;
import com.tdea.apptdea.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Inserta cuentas de prueba al arrancar la aplicación, solo si la tabla
 * {@code accounts} está vacía.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final AccountRepository accountRepository;

    /**
     * Crea el sembrador de datos.
     *
     * @param accountRepository repositorio de cuentas
     */
    public DataSeeder(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Inserta 4 cuentas de prueba si aún no hay cuentas registradas.
     *
     * @param args argumentos de la línea de comandos (no se usan)
     */
    @Override
    @Transactional
    public void run(String... args) {
        if (accountRepository.count() > 0) {
            log.info("La tabla accounts ya tiene datos; no se insertan cuentas de prueba");
            return;
        }

        List<Account> accounts = List.of(
                new Account("1000000001", "Ana María Gómez", new BigDecimal("1500000.00")),
                new Account("1000000002", "Carlos Andrés Restrepo", new BigDecimal("850000.50")),
                new Account("1000000003", "Laura Valentina Ríos", new BigDecimal("3200000.00")),
                new Account("1000000004", "Julián David Torres", new BigDecimal("125000.75"))
        );
        accountRepository.saveAll(accounts);
        log.info("Se insertaron {} cuentas de prueba", accounts.size());
    }
}
