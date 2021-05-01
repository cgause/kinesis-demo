package com.example.kinesisdemo;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
		return new ObjectMapper()
				.registerModule(new JavaTimeModule());
	}

	/*@Override
	public void initialize(GenericApplicationContext context) {
		final AmazonS3 s3Client = s3Client();
		final ObjectMapper mapper = defaultObjectMapper();
		MetricService metricService = new S3MetricService(s3Client, mapper);
		MetricEventConsumer eventConsumer = new MetricEventConsumer(metricService, mapper);
		context.registerBean("metricConsumer", FunctionRegistration.class,
				() -> new FunctionRegistration<Consumer<KinesisEvent>>(eventConsumer)
						.type(FunctionType.from(KinesisEvent.class).to(Void.class)));
	}*/
}
