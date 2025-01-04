package com.demo.auth.core.configurations;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "security.jwt")
@Data
@Validated
public class JwtConfig {
    @NotNull
    @Size(min = 50)
    private String secret;

    @PositiveOrZero
    private int accessTokenExpiration;

    @PositiveOrZero
    private int refreshTokenExpiration;
}
