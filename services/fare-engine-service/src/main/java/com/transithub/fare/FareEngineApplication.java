package com.transithub.fare;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@SpringBootApplication
@EnableDiscoveryClient
public class FareEngineApplication {
    public static void main(String[] args) { SpringApplication.run(FareEngineApplication.class, args); }
}
