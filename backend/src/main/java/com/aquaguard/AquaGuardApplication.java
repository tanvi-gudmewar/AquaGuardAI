package com.aquaguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AquaGuardApplication {
    public static void main(String[] args) {
        SpringApplication.run(AquaGuardApplication.class, args);
    }
}
