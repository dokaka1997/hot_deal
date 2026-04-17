package com.hotdeal.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class HotDealApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotDealApplication.class, args);
    }
}
