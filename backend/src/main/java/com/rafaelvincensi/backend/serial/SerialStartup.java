package com.rafaelvincensi.backend.serial;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SerialStartup implements ApplicationRunner {

    private final SerialService serialService;

    public SerialStartup(SerialService serialService) {
        this.serialService = serialService;
    }

    @Override
    public void run(ApplicationArguments args) {
        serialService.listarPortas();
    }
}
