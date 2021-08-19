package ru.drsanches.life_together.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;
import ru.drsanches.life_together.exception.AuthException;
import ru.drsanches.life_together.token.TokenService;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

public class TokenFilter extends GenericFilterBean {

    private final TokenService TOKEN_SERVICE;

    private final Pattern EXCLUDE_URI_PATTERN;

    public TokenFilter(TokenService tokenService, Pattern excludeUriPattern) {
        this.TOKEN_SERVICE = tokenService;
        this.EXCLUDE_URI_PATTERN = excludeUriPattern;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse  httpResponse = (HttpServletResponse) response;
        String token = httpRequest.getHeader("Authorization");
        String uri = httpRequest.getRequestURI();
        if (!EXCLUDE_URI_PATTERN.matcher(uri).matches()) {
            try {
                TOKEN_SERVICE.validate(token);
            } catch (AuthException e) {
                httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                httpResponse.getOutputStream().flush();
                httpResponse.getOutputStream().println("Wrong token");
            }
        }
        chain.doFilter(request, response);
    }
}