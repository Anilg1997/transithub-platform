package com.transithub.refund;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@SpringBootApplication
@EnableDiscoveryClient
public class RefundServiceApplication {
    public static void main(String[] args) { SpringApplication.run(RefundServiceApplication.class, args); }
}
