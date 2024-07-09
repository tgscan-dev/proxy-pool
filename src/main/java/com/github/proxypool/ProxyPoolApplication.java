package com.github.proxypool;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(
		servers = @Server(url = "/")
)
@SpringBootApplication
@EnableScheduling
public class ProxyPoolApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProxyPoolApplication.class, args);
	}

}
