package ru.drsanches.life_together.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import ru.drsanches.life_together.config.filter.AdminFilter;
import ru.drsanches.life_together.config.filter.LogFilter;
import ru.drsanches.life_together.config.filter.TokenFilter;
import ru.drsanches.life_together.common.token.TokenService;
import ru.drsanches.life_together.common.token.TokenSupplier;
import java.util.regex.Pattern;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //TODO: Refactor
    private final Pattern EXCLUDE_URI_PATTERN = Pattern.compile("/api/v1/auth/registration.*|/api/v1/auth/login.*" +
            "|/api/v1/auth/refreshToken.*|/ui.*|/favicon.ico|/h2-console.*" +
            //Swagger
            "|/swagger-ui.html.*|/webjars/springfox-swagger-ui/.*|/configuration/ui|/configuration/security" +
            "|/images/favicon-32x32.png|/images/favicon-16x16.png|/swagger-resources|/v2/api-docs.*");

    private final Pattern ADMIN_URI_PATTERN = Pattern.compile("/h2-console.*|/swagger-ui.html.*");

    private final Pattern LOG_URI_PATTERN = Pattern.compile("/api.*|/h2-console.*|/swagger-ui.html.*");

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenSupplier tokenSupplier;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(new TokenFilter(tokenService, EXCLUDE_URI_PATTERN), BasicAuthenticationFilter.class);
        http.addFilterAfter(new AdminFilter(tokenSupplier, ADMIN_URI_PATTERN), TokenFilter.class);
        http.addFilterAfter(new LogFilter(tokenSupplier, LOG_URI_PATTERN), AdminFilter.class);
        http.csrf().disable()
                .headers().frameOptions().disable()
                .addHeaderWriter(new StaticHeadersWriter("X-FRAME-OPTIONS", "SAMEORIGIN"))
                .and()
                .authorizeRequests()
                .anyRequest().permitAll();
    }
}