package com.distributed;

import com.distributed.registry.RegistryHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Scanner;

/**
 * @author Ray
 */
@SpringBootApplication
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class OrderServiceApplication implements CommandLineRunner {

    private final RegistryHolder registryHolder;

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        registryHolder.register();
        log.info("Registry service started, Press any key to stop.");
        if (new Scanner(System.in).next() != null) {
            log.info("Shutting down registry service.");
            registryHolder.unregister();
            System.exit(0);
        }
    }
}
