package com.t1.marselmkh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClientProcessingApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientProcessingApplication.class, args);
    }
}
