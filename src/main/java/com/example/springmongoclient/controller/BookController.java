package com.example.springmongoclient.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springmongoclient.model.Book;
import com.example.springmongoclient.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;



@RestController
public class BookController {
	@Autowired
	private BookRepository bookRepository;	
	 @Autowired
	 private MongoTemplate mongoTemplate;
	
	@GetMapping("/books")
	public ResponseEntity<?> getAllBooks(){
		List<Book> books=bookRepository.findAll();
		if (books.size()>0) {
			return new ResponseEntity<List<Book>>(books,HttpStatus.OK);
		}else {
			return new ResponseEntity<>("No Books Found",HttpStatus.NOT_FOUND);
		}
	}
	@PostMapping("/books")
	public ResponseEntity<?> saveBook(@RequestBody Book book){
		try {
			Book book2=bookRepository.save(book);
			
			return new ResponseEntity<Book>(book2,HttpStatus.CREATED);
		} catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(e.getMessage(),HttpStatus.NO_CONTENT);
		}
	}
	@DeleteMapping("/books/{id}")
	public ResponseEntity<?> deleteBook(@PathVariable String id){
		Optional<Book> book=bookRepository.findById(id);
		if (book!=null) {
			bookRepository.deleteById(id);
			return new ResponseEntity<>("BOok Deleted Successfully",HttpStatus.OK);
		}else {
			return new ResponseEntity<>("Book Not Found",HttpStatus.NOT_FOUND);
		}
	}
	@GetMapping("/findBook/{id}")
	public ResponseEntity<?> findBook(@PathVariable String id){
		try {
			Optional<Book> book=bookRepository.findById(id);
			return new ResponseEntity<Book>(book.get(),HttpStatus.OK);
		} catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
		}
		
	}
	@PutMapping("/updateBook/{id}")
	public ResponseEntity<?> updateBook(@RequestBody Book book){
		try {
			bookRepository.save(book);
			return new ResponseEntity<Book>(book,HttpStatus.OK);
		} catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
		}
	}
	@PutMapping("/updateCastWithBookId/{id}")
	public ResponseEntity<?> updateCastWithBookId(@PathVariable String id,@RequestBody Book book){
		try {
			Query query=Query.query(Criteria.where("id").is(id));
			Book book1=mongoTemplate.findOne(query, Book.class, "book");
			book1.setCast(book.getCast());
			bookRepository.save(book1);
			return new ResponseEntity<Book>(book1,HttpStatus.OK);
		} catch (Exception e) {
			// TODO: handle exception
			return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
		}
	}
	@GetMapping("/books/{name}/{rating}")
	public ResponseEntity<?>getBookByAuthor(@PathVariable String name,@PathVariable double rating){
		Query query=new Query().addCriteria(Criteria.where("bookAuthor").is(name).and("rating").gte(rating));
		List<Book>books=mongoTemplate.find(query,Book.class);
		if (books!=null&&books.size()>0) {
			return new ResponseEntity<List<Book>>(books,HttpStatus.FOUND);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		
	}
	@GetMapping("/oneBook/{name}")
	public ResponseEntity<?> getOneBookByAuthor(@PathVariable String name){
		Query query=new Query().addCriteria(Criteria.where("bookAuthor").is(name));
		Book book=mongoTemplate.findOne(query, Book.class);
		if (book!=null) {
			return new ResponseEntity<Book>(book,HttpStatus.FOUND);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
	}
	@GetMapping("/bookByGenre/{genre1}/{genre2}")
	public ResponseEntity<?> getBookByGenre(@PathVariable String genre1,@PathVariable String genre2){
		List<String> genres=Arrays.asList(genre1,genre2);
		Query query=new Query().addCriteria(Criteria.where("genre").in(genres));
		List<Book> books=mongoTemplate.find(query, Book.class);
		if (!books.isEmpty()) {
			return new ResponseEntity<List<Book>>(books,HttpStatus.FOUND);
		}
		return new ResponseEntity<>("No book Found in the respective Genre",HttpStatus.NOT_FOUND);
	}
	@GetMapping("/bookByGenreAndAuthor")
	public ResponseEntity<?> getBookByGenreAndAuthor(@RequestParam String bookAuthor,@RequestParam String genre){
		Query query=new Query().addCriteria(Criteria.where("genre").is(genre).and("bookAuthor").is(bookAuthor));
		query.fields().exclude("rating","genre","_class");
		List<Document> books=mongoTemplate.find(query, Document.class, "book");
		if (!books.isEmpty()) {
			return new ResponseEntity<List<Document>>(books,HttpStatus.FOUND);
		}
		return new ResponseEntity<>("No book Found in the respective Genre And Author Name",HttpStatus.NOT_FOUND);
	}
	@PutMapping("/updateAParticularField/{id}")
	public ResponseEntity<?> updateAParticularField(@PathVariable String id, @RequestBody Book book) {
		Query query=Query.query(Criteria.where("_id").is(id));
		Book book2=mongoTemplate.findAndModify(query, getUpdate(book), Book.class);
		query.fields().exclude("_id","_class");
		Document document=mongoTemplate.findOne(query, Document.class, "book");
		if (book2!=null) {
			return new ResponseEntity<Document>(document,HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	private Update getUpdate(Book book) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> bookMap = mapper.convertValue(book, Map.class);
		Update update = new Update();
		for (Map.Entry<String, Object> entry: bookMap.entrySet()) {
			if ((!entry.getKey().equals("id"))&&(entry.getValue()!=null)) {
		        update.set(entry.getKey(), entry.getValue());
		    }
			System.out.println(entry.getKey());
		}
		return update;
	}
	@PatchMapping("/updateAuthorGender")
	public ResponseEntity<?> updateMultipleBooks(@RequestParam(name="id") String[] keyValues,@RequestParam String authorGender){
		List<Document>documents=new ArrayList<>();
		for (String values : keyValues) {
			Query query=Query.query(Criteria.where("_id").is(values));
			Update update=new Update();
			update.set("authorGender", authorGender);
			mongoTemplate.updateFirst(query, update, "book");
			query.fields().exclude("_class");
			Document document=mongoTemplate.findOne(query, Document.class,"book");
			documents.add(document);
		}
		return new ResponseEntity<List<Document>>(documents,HttpStatus.OK);
	}
	@GetMapping("getByAuthorGender/{authorGender1}")
	public ResponseEntity<?> getByAuthorGender(@PathVariable String authorGender1){
		Collection<Book>collection=bookRepository.findAll();
		if (collection.size()>0) {
			List<Book> list=collection.stream().filter(book -> book.getAuthorGender().equalsIgnoreCase(authorGender1)).toList();
			return new ResponseEntity<Collection<Book>>(list,HttpStatus.FOUND);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
}
