package com.example.fiap.videoslice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableScheduling
public class VideoSliceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoSliceApiApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onAppStart(ApplicationReadyEvent applicationReadyEvent) {

    }

}
