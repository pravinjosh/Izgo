package com.example.springmongoclient.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection= "book")
public class Book {
	@Id
	private String id;
	private String bookName;
	private String bookAuthor;
	private String genre[];
	private Double rating;
	private Cast cast;
	private String authorGender;
}
