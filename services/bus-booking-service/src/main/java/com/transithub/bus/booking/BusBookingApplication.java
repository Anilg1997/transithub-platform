package com.transithub.bus.booking;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@SpringBootApplication
@EnableDiscoveryClient
public class BusBookingApplication {
    public static void main(String[] args) { SpringApplication.run(BusBookingApplication.class, args); }
}
