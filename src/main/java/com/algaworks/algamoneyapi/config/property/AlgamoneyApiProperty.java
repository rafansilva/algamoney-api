package com.algaworks.algamoneyapi.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties("algamoney")
public class AlgamoneyApiProperty {

    private String origemPermitida;

    private final Seguranca seguranca = new Seguranca();

    private final Mail mail = new Mail();

    @Getter
    @Setter
    public static class Seguranca {

        private List<String> redirectsPermitidos;

        private String authServerUrl;
    }

    @Getter
    @Setter
    public static class Mail {

        private String host;

        private Integer port;

        private String username;

        private String password;
    }
}
