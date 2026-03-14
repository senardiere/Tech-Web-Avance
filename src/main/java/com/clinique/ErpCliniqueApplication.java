package com.clinique;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class ErpCliniqueApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ErpCliniqueApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ErpCliniqueApplication.class, args);
        System.out.println(" ERP Clinique démarré sur http://localhost:8081");
    }
}