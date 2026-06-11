package com.transithub.bus.tracking;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@SpringBootApplication
@EnableDiscoveryClient
public class BusTrackingApplication {
    public static void main(String[] args) { SpringApplication.run(BusTrackingApplication.class, args); }
}
