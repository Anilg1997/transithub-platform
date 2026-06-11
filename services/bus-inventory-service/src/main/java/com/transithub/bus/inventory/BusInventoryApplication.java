package com.transithub.bus.inventory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BusInventoryApplication {
    public static void main(String[] args) { SpringApplication.run(BusInventoryApplication.class, args); }
}
