package org.threepixeldev.saungeraclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.threepixeldev.saungeraclient.security.utils.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class SaungeraclientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaungeraclientApplication.class, args);
	}

}
