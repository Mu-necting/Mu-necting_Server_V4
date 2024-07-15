package com.munecting.api.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    @Bean
    public OpenAPI springBootAPI() {
        Info info = new Info()
                .title("Mu-necting")
                .description("음악으로 연결하다 🎵 Mu-necting V4 API 문서")
                .contact(new Contact().name("전민주").url("https://github.com/mingmingmon").email("mingmingmon@naver.com"));

        Server server = new Server().url("/");

        return new OpenAPI()
                .servers(List.of(server))
                .info(info);
    }

}