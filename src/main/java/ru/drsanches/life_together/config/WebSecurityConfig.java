package ru.drsanches.life_together.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import ru.drsanches.life_together.security.AdminFilter;
import ru.drsanches.life_together.security.TokenFilter;
import ru.drsanches.life_together.service.utils.UserDetailsServiceImpl;
import ru.drsanches.life_together.service.utils.UserPermissionService;
import ru.drsanches.life_together.token.TokenService;
import java.util.regex.Pattern;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //TODO: Refactor
    private final Pattern EXCLUDE_URI_PATTERN = Pattern.compile("/api/v1/auth/registration.*|/api/v1/auth/login.*" +
            "|/api/v1/auth/refreshToken.*|/ui.*|/h2-console.*" +
            //Swagger
            "|/swagger-ui.html.*|/webjars/springfox-swagger-ui/.*|/configuration/ui|/configuration/security" +
            "|/images/favicon-32x32.png|/images/favicon-16x16.png|/swagger-resources|/v2/api-docs.*");

    private final Pattern ADMIN_URI_PATTERN = Pattern.compile("/h2-console.*|/swagger-ui.html.*");

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserPermissionService userPermissionService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(new TokenFilter(tokenService, EXCLUDE_URI_PATTERN), BasicAuthenticationFilter.class);
        http.addFilterAfter(new AdminFilter(tokenService, userPermissionService, ADMIN_URI_PATTERN), TokenFilter.class);
        http.csrf().disable()
                .headers().frameOptions().disable()
                .addHeaderWriter(new StaticHeadersWriter("X-FRAME-OPTIONS", "SAMEORIGIN"))
                .and()
                .authorizeRequests()
                .anyRequest().permitAll();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}