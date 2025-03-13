package com.pape.timetodo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TimetodoApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(TimetodoApplication.class);
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);

	}

}
