package com.philokun.stardustbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.philokun.stardustbackend.mapper")
public class StardustBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(StardustBackendApplication.class, args);
    }
}
