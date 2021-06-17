package com.distributed;

import com.distributed.registry.RegistryHolder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;
import java.util.logging.Logger;

/**
 * @author Ray
 */
@SpringBootApplication
public class LogServiceApplication implements CommandLineRunner {

    private static final Logger log = Logger.getLogger(LogServiceApplication.class.getName());
    private final RegistryHolder registryHolder;

    public LogServiceApplication(RegistryHolder registryHolder) {
        this.registryHolder = registryHolder;
    }

    public static void main(String[] args) {
        SpringApplication.run(LogServiceApplication.class, args);
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
