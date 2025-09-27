package com.peppeosmio.lockate;

import com.peppeosmio.lockate.anonymous_group.jobs.LocationRetentionJob;
import com.peppeosmio.lockate.api_key.ApiKeyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.locks.Lock;

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
