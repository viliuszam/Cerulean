package me.vilius.cerulean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CeruleanApplication {

	public static void main(String[] args) {
		SpringApplication.run(CeruleanApplication.class, args);
	}

}
