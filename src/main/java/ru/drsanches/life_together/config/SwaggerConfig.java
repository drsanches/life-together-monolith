package ru.drsanches.life_together.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket publicApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("public-api")
                .ignoredParameterTypes(ApiIgnore.class)
                .apiInfo(apiInfo("Life Together public API", "Life Together API for users"))
                .select().paths(or(
                        regex("/actuator/health"), //TODO: Rename
                        regex("/api/v1/auth.*"),
                        regex("/api/v1/profile.*"),
                        regex("/api/v1/friends.*"),
                        regex("/api/v1/debts.*"),
                        regex("/api/v1/image.*")))
                .build();
    }

    @Bean
    public Docket adminApi() {
        //TODO: Add h2-console and swagger-ui to the paths
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("admin-api")
                .ignoredParameterTypes(ApiIgnore.class)
                .apiInfo(apiInfo("Life Together admin API",
                        "Life Together API for admins and developers<br><br>" +
                                "<b>Other links:</b><br>" +
                                "<a href=\"/h2-console\">/h2-console</a> - db access<br>" +
                                "<a href=\"/swagger-ui.html\">/swagger-ui.html</a> - this page"))
                .select().paths(and(regex("/actuator.*"), not(regex("/actuator/health.*"))))
                .build();
    }

    private ApiInfo apiInfo(String title, String description) {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .version("1.0")
                .build();
    }
}