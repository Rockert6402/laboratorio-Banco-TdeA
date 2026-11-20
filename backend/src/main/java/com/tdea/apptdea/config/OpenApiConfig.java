package com.tdea.apptdea.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metadatos de la documentación OpenAPI mostrada en Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Define el título, la descripción y la versión de la API.
     *
     * @return definición OpenAPI con la información del servicio
     */
    @Bean
    public OpenAPI bankOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Banco TdeA API")
                .description("Cuentas, transferencias atómicas e historial de transacciones con comprobante QR")
                .version("0.0.1"));
    }
}
