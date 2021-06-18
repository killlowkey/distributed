package com.distributed.registry;

import com.distributed.annotation.ConditionalOnNotRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * 服务注册
 *
 * @author Ray
 * @date created in 2021/6/18 10:17
 */
@Component
@RequiredArgsConstructor
@ConditionalOnNotRegistry
@Slf4j
public class RegistryServiceHook implements CommandLineRunner {

    private final RegistryHolder registryHolder;

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
