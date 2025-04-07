package com.laresencanto.laresencantorestapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LaresEncantoRestApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaresEncantoRestApiApplication.class, args);
    }

}
