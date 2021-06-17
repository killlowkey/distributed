package com.distributed;

import com.distributed.registry.RegistryHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

/**
 * @author Ray
 * @date created in 2021/6/17 19:50
 */
@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class GatewayApplication implements CommandLineRunner {

    private final RegistryHolder registryHolder;

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
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
