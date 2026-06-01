package com.example.empresasapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EmpresasApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmpresasApiApplication.class, args);
    }
}