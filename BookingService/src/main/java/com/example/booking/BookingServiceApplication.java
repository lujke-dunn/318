package com.example.booking;



// BookingServiceApplication.java


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class BookingServiceApplication {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(BookingServiceApplication.class);
		app.setDefaultProperties(Collections.singletonMap("server.port", "8082"));
		app.run(args);
	}
}
