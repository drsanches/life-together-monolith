package ru.drsanches.life_together.config.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;
import ru.drsanches.life_together.exception.auth.AuthException;
import ru.drsanches.life_together.integration.token.TokenService;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Pattern;

public class FilterLogger extends GenericFilterBean {

    private final static Logger LOG = LoggerFactory.getLogger(FilterLogger.class);

    private final static String MESSAGE_PATTERN = "URL: {}, Address: {}, UserId: {}";

    private final TokenService TOKEN_SERVICE;

    private final Pattern LOG_URI_PATTERN;

    public FilterLogger(TokenService tokenService, Pattern logUriPattern) {
        this.TOKEN_SERVICE = tokenService;
        this.LOG_URI_PATTERN = logUriPattern;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = TOKEN_SERVICE.getTokenFromRequest(httpRequest);

        if (LOG_URI_PATTERN.matcher(httpRequest.getRequestURI()).matches()) {
            try {
                LOG.info(MESSAGE_PATTERN, httpRequest.getRequestURL(), httpRequest.getRemoteAddr(), TOKEN_SERVICE.getUserId(token));
            } catch (AuthException e) {
                LOG.info(MESSAGE_PATTERN, httpRequest.getRequestURL(), httpRequest.getRemoteAddr(), "unauthorized");
            }
        }
        chain.doFilter(request, response);
    }
}