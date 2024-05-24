package ru.gorbunov.springboot.app.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Spring Boot App",
                description = "Приложение позволяющее измерить погоду с помощью сенсора", version = "2.2.8",
                contact = @Contact(
                        name = "Gorbunov maksim"
                )
        )
)

public class configSwagger {
}
