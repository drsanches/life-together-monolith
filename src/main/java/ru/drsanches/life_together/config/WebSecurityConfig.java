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
import java.util.function.Predicate;
import static java.util.function.Predicate.not;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final Predicate<String> PUBLIC_URI = ((Predicate<String>)
            regex("/api/v1/auth/registration.*")::apply)
            .or(regex("/api/v1/auth/login.*")::apply)
            .or(regex("/api/v1/auth/refreshToken.*")::apply)
            .or(regex("/actuator/health.*")::apply)
            .or(regex("/ui.*")::apply)
            .or(regex("/favicon.ico")::apply);

    private final Predicate<String> ADMIN_URI = ((Predicate<String>)
            regex("/h2-console.*")::apply)
            .or(regex("/swagger-ui.html.*")::apply)
            .or(((Predicate<String>)
                    regex("/actuator.*")::apply)
                    .and(not(regex("/actuator/health.*")::apply)));

    private final Predicate<String> LOG_URI = ((Predicate<String>)
            regex("/api.*")::apply)
            .or(regex("/h2-console.*")::apply)
            .or(regex("/swagger-ui.html.*")::apply)
            .or(regex("/actuator.*")::apply);

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenSupplier tokenSupplier;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(new TokenFilter(tokenService, PUBLIC_URI), BasicAuthenticationFilter.class);
        http.addFilterAfter(new AdminFilter(tokenSupplier, ADMIN_URI), TokenFilter.class);
        http.addFilterAfter(new LogFilter(tokenSupplier, LOG_URI), AdminFilter.class);
        http.csrf().disable()
                .headers().frameOptions().disable()
                .addHeaderWriter(new StaticHeadersWriter("X-FRAME-OPTIONS", "SAMEORIGIN"))
                .and()
                .authorizeRequests()
                .anyRequest().permitAll();
    }
}