package com.peppeosmio.lockate;

import com.peppeosmio.lockate.api_key.ApiKeyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.locks.Lock;

@SpringBootApplication
public class LockateApplication implements CommandLineRunner {

    private final ApiKeyService apiKeyService;


    public LockateApplication(
                              ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    public static void main(String[] args) {
        SpringApplication.run(LockateApplication.class, args);
    }

    @Override
    public void run(String... args) {
        // You can check command-line arguments to limit this to a specific flag
        if (args.length > 0 && args[0].equals("create-api-key")) {
            var apiKey = apiKeyService.createApiKey();
            System.out.println(apiKey.key().toString());
            System.exit(0);
        }
    }
}
