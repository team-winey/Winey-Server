package org.winey.server.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Winey 서버의 클라 호감작 Swagger")
                .description("Winey 안드, 아요 화이팅!")
                .version("1.0.0");


        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}
