package com.example.kinesisdemo;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class KinesisDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(KinesisDemoApplication.class, args);
	}

	@Bean
	public AmazonS3 s3Client() {
		return AmazonS3Client.builder().withRegion(Regions.US_EAST_1).build();
	}

	@Bean
	public ObjectMapper defaultObjectMapper() {
		return new ObjectMapper();
	}

}
