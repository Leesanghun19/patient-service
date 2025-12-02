package com.heuron.patient_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI patientServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Patient Service API")
                .description("병원에서 근무하는 의사가 환자의 병변 분석을 위해 환자 기본 정보와 이미지 파일을 업로드하여 저장하고 조회할 수 있는 REST API")
                .version("1.0.0"))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Local Development Server")
            ));
    }
}