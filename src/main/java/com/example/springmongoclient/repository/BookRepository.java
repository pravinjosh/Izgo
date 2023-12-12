package com.example.springmongoclient.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.springmongoclient.model.Book;



public interface BookRepository extends MongoRepository<Book, String>{

}
