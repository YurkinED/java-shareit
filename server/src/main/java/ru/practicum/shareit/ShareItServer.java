package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZoneId;

@SpringBootApplication
public class ShareItServer {
	public static final ZoneId zoneIdGlobal=ZoneId.of("UTC+03");

	public static void main(String[] args) {
		SpringApplication.run(ShareItServer.class, args);
	}

}
