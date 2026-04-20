package com.algaworks.algamoneyapi.config;

import com.algaworks.algamoneyapi.config.token.CustomTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Arrays;

@SuppressWarnings("deprecation")
@Profile("oauth-security")
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                    .withClient("angular")
                    .secret("$2a$10$xyWWm5qQx4Bo8xroD/ZzresuIcmjXxnb1UrOwq02s/keYwpsm/Fiy") // @ngul@r0
                    .scopes("read", "write")
                    .authorizedGrantTypes("password", "refresh_token")
                    .accessTokenValiditySeconds(1800)
                .and()
                    .withClient("mobile")
                    .secret("$2a$10$xyWWm5qQx4Bo8xroD/ZzresuIcmjXxnb1UrOwq02s/keYwpsm/Fiy") // m0b1l30
                    .scopes("read")
                    .authorizedGrantTypes("password", "refresh_token")
                    .accessTokenValiditySeconds(1800) // 1800 = 30 minutos
                    .refreshTokenValiditySeconds(3600 * 24); // 3600 * 24 = 1 dia
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));

        endpoints.authenticationManager(authenticationManager)
                .tokenEnhancer(tokenEnhancerChain)
                .tokenStore(tokenStore())
                .userDetailsService(userDetailsService)
                .reuseRefreshTokens(false);
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
        accessTokenConverter.setSigningKey("3032885ba9cd6621bcc4e7d6b6c35c2b");
        return accessTokenConverter;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer();
    }
}
