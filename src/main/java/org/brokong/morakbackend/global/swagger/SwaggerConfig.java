package org.brokong.morakbackend.global.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
	info = @Info(
		title = "Morak Backend API",
		description = "모락 백엔드 API 문서",
		version = "1.0.0"
	)
)
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.components(new Components()
							.addSecuritySchemes("bearer-jwt",
												new SecurityScheme()
													.type(SecurityScheme.Type.HTTP)
													.scheme("bearer")
													.bearerFormat("JWT")
													.in(SecurityScheme.In.HEADER)
													.name("Authorization")
							)
			)
			.addSecurityItem(
				new SecurityRequirement().addList("bearer-jwt")
			);
	}
}