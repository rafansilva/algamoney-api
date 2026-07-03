package com.algaworks.algamoneyapi.config.property;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties("algamoney")
public class AlgamoneyApiProperty {

    private final Seguranca seguranca = new Seguranca();

    private final Mail mail = new Mail();

    @Data
    public static class Seguranca {

        private boolean enableHttps;

        private String origemPermitida = "http://localhost:8000";
    }

    @Data
    public static class Mail {

        private String host;

        private Integer port;

        private String username;

        private String password;
    }
}
