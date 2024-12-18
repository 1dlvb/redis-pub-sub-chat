package com.dlvb.redischat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class RedisChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisChatApplication.class, args);
    }

}
