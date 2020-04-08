package com.fsg.fsgdata.eiprestlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EipRestletApplication {

	public static void main(String[] args) {
		SpringApplication.run(EipRestletApplication.class, args);
	}

}
