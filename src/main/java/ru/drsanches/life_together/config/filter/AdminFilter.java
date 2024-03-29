package ru.drsanches.life_together.config.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;
import ru.drsanches.life_together.common.token.TokenSupplier;
import ru.drsanches.life_together.common.token.data.Role;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Predicate;

public class AdminFilter extends GenericFilterBean {

    private final static Logger LOG = LoggerFactory.getLogger(AdminFilter.class);

    private final TokenSupplier TOKEN_SUPPLIER;

    private final Predicate<String> ADMIN_URI;

    public AdminFilter(TokenSupplier tokenSupplier, Predicate<String> adminUri) {
        this.TOKEN_SUPPLIER = tokenSupplier;
        this.ADMIN_URI = adminUri;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse  httpResponse = (HttpServletResponse) response;
        String uri = httpRequest.getRequestURI();
        if (ADMIN_URI.test(uri)
                && (TOKEN_SUPPLIER.get() == null || !Role.ADMIN.equals(TOKEN_SUPPLIER.get().getRole()))) {
            LOG.info("User {} have no permissions for uri '{}'",
                    TOKEN_SUPPLIER.get() == null ? "unauthorized" : TOKEN_SUPPLIER.get().getUserId(), uri);
            httpResponse.setStatus(HttpStatus.FORBIDDEN.value());
            httpResponse.getOutputStream().flush();
            httpResponse.getOutputStream().println("You do not have permission");
            return;
        }
        chain.doFilter(request, response);
    }
}