package com.transithub.lock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@SpringBootApplication
@EnableDiscoveryClient
public class InventoryLockApplication {
    public static void main(String[] args) { SpringApplication.run(InventoryLockApplication.class, args); }
}
