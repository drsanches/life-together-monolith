package ru.drsanches.life_together.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Life Together public API")
                .description("<font size=\"+1\">" +
                        "Life Together API for admins and developers<br><br>" +
                        "<b>Public health check:</b> <a href=\"/actuator/health\">/actuator/health</a><br><br>" +
                        "<b>Admin links:</b><br>" +
                        "<a href=\"/h2-console\">/h2-console</a> - db access<br>" +
                        "<a href=\"/actuator\">/actuator</a> - monitoring endpoints<br>" +
                        "<a href=\"/swagger-ui.html\">/swagger-ui.html</a> - this page" +
                        "</font>"
                )
                .version("1.0");
    }
}