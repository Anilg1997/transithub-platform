package com.transithub.train.booking;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@SpringBootApplication
@EnableDiscoveryClient
public class TrainBookingApplication {
    public static void main(String[] args) { SpringApplication.run(TrainBookingApplication.class, args); }
}
