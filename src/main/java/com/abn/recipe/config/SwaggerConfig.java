package com.abn.recipe.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        var info = new Info()
                .title("API ABN Recipes")
                .version("0.0.1-SNAPSHOT")
                .description("This is the documentation for the API ABN Recipes");

        return new OpenAPI().info(info);
    }
}
