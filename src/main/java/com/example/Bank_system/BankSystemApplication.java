package com.example.Bank_system;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Bank System Application",
				description = "Backend rest API",
				version = "v1.0",
				contact = @Contact(
						name = "Peter",
						email = "peterhamza6@gmail.com",
						url = "https://github.com/Peterhamz"
				),
				license = @License(
						name = "PHJAMES",
						url = "https://github.com/Peterhamz"
				)

				),
		externalDocs = @ExternalDocumentation(
				description = "Java Bank app Documentation",
				url = "https://github.com/Peterhamz/Banking_system"
		)
)
public class BankSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankSystemApplication.class, args);
	}

}
