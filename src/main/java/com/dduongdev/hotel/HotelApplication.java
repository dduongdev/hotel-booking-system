package com.dduongdev.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.dduongdev.hotel.security.config.SecurityProperties;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableConfigurationProperties(SecurityProperties.class)
public class HotelApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelApplication.class, args);
	}

}
