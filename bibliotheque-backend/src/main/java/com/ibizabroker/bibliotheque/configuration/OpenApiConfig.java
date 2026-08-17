package com.ibizabroker.bibliotheque.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bibliothequeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Bibliotheque")
                        .description("API REST de gestion de bibliotheque : livres, utilisateurs et emprunts")
                        .version("1.0"));
    }
}
