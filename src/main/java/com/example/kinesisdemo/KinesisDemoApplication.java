package com.example.kinesisdemo;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.kinesisdemo.function.MetricEventConsumer;
import com.example.kinesisdemo.service.MetricService;
import com.example.kinesisdemo.service.S3MetricService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cloud.function.context.FunctionRegistration;
import org.springframework.cloud.function.context.FunctionType;
import org.springframework.cloud.function.context.FunctionalSpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.messaging.Message;

@SpringBootConfiguration
public class KinesisDemoApplication implements ApplicationContextInitializer<GenericApplicationContext> {

	public static void main(String[] args) {
		FunctionalSpringApplication.run(KinesisDemoApplication.class, args);
	}

	public AmazonS3 s3Client() {
		return AmazonS3Client.builder().withRegion(Regions.US_EAST_1).build();
	}

	public ObjectMapper defaultObjectMapper() {
		return new ObjectMapper().registerModule(new JavaTimeModule());
	}

	MetricService s3MetricService() {
		return new S3MetricService(s3Client(), defaultObjectMapper());
	}

	@Override
	public void initialize(GenericApplicationContext genericApplicationContext) {
		genericApplicationContext.registerBean("metricConsumer", FunctionRegistration.class,
				() -> new FunctionRegistration<>(new MetricEventConsumer(s3MetricService()))
						.type(FunctionType.from(Message.class).to(Void.class)));
	}
}
