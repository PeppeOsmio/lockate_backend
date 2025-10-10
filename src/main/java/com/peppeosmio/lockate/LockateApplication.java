package com.peppeosmio.lockate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LockateApplication implements CommandLineRunner {

    public LockateApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(LockateApplication.class, args);
    }

    @Override
    public void run(String... args) {
    }
}
