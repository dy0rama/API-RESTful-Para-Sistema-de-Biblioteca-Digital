package com.example.biblioteca.digital.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI bibliotecaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema de Biblioteca Digital API").version("1.0")
                        .description("API RESTful para gerenciamento de livros, " + "usuários e empréstimos.")
                        .contact(new Contact().name("Rodrigo")))
                .components(new Components().addSecuritySchemes("oauth2",
                        new SecurityScheme().type(SecurityScheme.Type.OAUTH2).description("OAuth2 Authorization Code " + "com PKCE")
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow().authorizationUrl("http://localhost:8080/oauth2/authorize")
                                                .tokenUrl("http://localhost:8080/oauth2/token")
                                                .scopes(new io.swagger.v3.oas.models.security.Scopes()
                                                        .addString("openid", "Acesso OpenID Connect")
                                                        .addString("profile", "Acesso ao perfil do usuário"))))))
                .addSecurityItem(new SecurityRequirement().addList("oauth2"));
    }
}
