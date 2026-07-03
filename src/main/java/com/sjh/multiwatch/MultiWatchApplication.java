package com.sjh.multiwatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class MultiWatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiWatchApplication.class, args);
    }

}
