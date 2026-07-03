package com.algaworks.algamoneyapi.security.token;

import org.apache.catalina.util.ParameterMap;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

@Profile("oauth-security")
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RefreshTokenCookiePreProcessorFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;

        if ("/oauth/token".equalsIgnoreCase(req.getRequestURI()) && "refresh_token".equals(req.getParameter("grant_type")) && req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                String refreshToken =
                        Stream.of(req.getCookies())
                                .filter(cookie1 -> "refresh_token".equals(cookie.getName()))
                                .findFirst()
                                .map(cookie1 -> cookie.getName())
                                .orElse(null);

                req = new MyServletRequestWrapper(req, refreshToken);
            }
        }

        filterChain.doFilter(req, servletResponse);
    }

    static class MyServletRequestWrapper extends HttpServletRequestWrapper {

        private final String refreshToken;

        public MyServletRequestWrapper(HttpServletRequest request, String refreshToken) {
            super(request);
            this.refreshToken = refreshToken;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            ParameterMap<String, String[]> map = new ParameterMap<>(getRequest().getParameterMap());
            map.put("refresh_token", new String[]{refreshToken});
            map.setLocked(true);
            return map;
        }
    }
}
