package ru.drsanches.life_together.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;
import ru.drsanches.life_together.service.utils.UserPermissionService;
import ru.drsanches.life_together.token.TokenService;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

public class AdminFilter extends GenericFilterBean {

    private final TokenService TOKEN_SERVICE;

    private final UserPermissionService USER_PERMISSION_SERVICE;

    private final Pattern ADMIN_URI_PATTERN;

    public AdminFilter(TokenService tokenService, UserPermissionService userPermissionService, Pattern adminUriPattern) {
        this.TOKEN_SERVICE = tokenService;
        this.USER_PERMISSION_SERVICE = userPermissionService;
        this.ADMIN_URI_PATTERN = adminUriPattern;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse  httpResponse = (HttpServletResponse) response;
        String token = httpRequest.getHeader("Authorization");
        if (token == null) {
            token = TOKEN_SERVICE.getAccessTokenFromCookies(httpRequest.getCookies());
        }
        String uri = httpRequest.getRequestURI();
        if (ADMIN_URI_PATTERN.matcher(uri).matches()) {
            String userId = TOKEN_SERVICE.getUserId(token);
            if (!USER_PERMISSION_SERVICE.isAdmin(userId)) {
                httpResponse.setStatus(HttpStatus.FORBIDDEN.value());
                httpResponse.getOutputStream().flush();
                httpResponse.getOutputStream().println("You do not have permission");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}