package com.tdea.apptdea.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración CORS para permitir que el frontend consuma la API.
 * <p>
 * Los orígenes salen de {@code app.cors.allowed-origins} (variable de entorno
 * {@code CORS_ALLOWED_ORIGINS}); por defecto {@code http://localhost:3000}.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] allowedOrigins;

    /**
     * Crea la configuración CORS.
     *
     * @param allowedOrigins orígenes permitidos, separados por coma
     */
    public CorsConfig(@Value("${app.cors.allowed-origins}") String[] allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    /**
     * Habilita CORS en todas las rutas para los orígenes configurados.
     *
     * @param registry registro de mapeos CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Location");
    }
}
