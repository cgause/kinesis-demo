package com.example.kinesisdemo;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.kinesisdemo.function.MetricEventConsumer;
import com.example.kinesisdemo.service.MetricService;
import com.example.kinesisdemo.service.S3MetricService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionRegistration;
import org.springframework.cloud.function.context.FunctionType;
import org.springframework.cloud.function.context.FunctionalSpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.messaging.Message;

@SpringBootApplication
public class KinesisDemoApplication implements ApplicationContextInitializer<GenericApplicationContext> {

	public static void main(String[] args) {
		FunctionalSpringApplication.run(KinesisDemoApplication.class, args);
	}

	@Bean
	public AmazonS3 s3Client() {
		return AmazonS3Client.builder().withRegion(Regions.US_EAST_1).build();
	}

	@Bean
	public ObjectMapper defaultObjectMapper() {
		return new ObjectMapper().registerModule(new JavaTimeModule());
	}

	@Autowired
	private MetricEventConsumer eventConsumer;

	@Override
	public void initialize(GenericApplicationContext genericApplicationContext) {
		genericApplicationContext.registerBean("metricConsumer", FunctionRegistration.class,
				() -> new FunctionRegistration<>(eventConsumer)
						.type(FunctionType.from(Message.class).to(Void.class)));
	}
}
