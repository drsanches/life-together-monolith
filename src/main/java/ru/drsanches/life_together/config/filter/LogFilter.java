package ru.drsanches.life_together.config.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;
import ru.drsanches.life_together.common.token.TokenSupplier;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Pattern;

public class LogFilter extends GenericFilterBean {

    private final static Logger LOG = LoggerFactory.getLogger(LogFilter.class);

    private final static String MESSAGE_PATTERN = "URL: {}, Address: {}, UserId: {}";

    private final TokenSupplier TOKEN_SUPPLIER;

    private final Pattern LOG_URI_PATTERN;

    public LogFilter(TokenSupplier tokenSupplier, Pattern logUriPattern) {
        this.TOKEN_SUPPLIER = tokenSupplier;
        this.LOG_URI_PATTERN = logUriPattern;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (LOG_URI_PATTERN.matcher(httpRequest.getRequestURI()).matches()) {
            if (TOKEN_SUPPLIER.get() != null) {
                LOG.info(MESSAGE_PATTERN, httpRequest.getRequestURL(), httpRequest.getRemoteAddr(), TOKEN_SUPPLIER.get().getUserId());
            } else {
                LOG.info(MESSAGE_PATTERN, httpRequest.getRequestURL(), httpRequest.getRemoteAddr(), "unauthorized");
            }
        }
        chain.doFilter(request, response);
    }
}