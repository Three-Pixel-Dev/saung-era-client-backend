package org.threepixeldev.saungeraclient.security.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    private String secret;
    private String issuer;
    private long accessTokenValidity;
    private long refreshTokenValidity;
}

