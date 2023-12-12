package com.example.springmongoclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringmongoclientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringmongoclientApplication.class, args);
	}
//	@Autowired
//	private MongoTemplate mongoTemplate;
//	
//	@PostConstruct
//	public void createDatabase() {
//	 String databaseName="dynamic-db-" + UUID.randomUUID();
//		try {
//			mongoTemplate.getDb().createCollection(databaseName);
//			System.out.println("Database created successfully");
//		} catch (Exception e) {
//			System.err.println("Failed to create database");
//		}
//	}

}
