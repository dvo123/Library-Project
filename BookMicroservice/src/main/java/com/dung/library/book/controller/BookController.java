package com.dung.library.book.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;

import com.dung.library.book.model.Book;
import com.dung.library.book.repository.BookRepository;
import com.dung.library.book.service.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);
    private final BookService bookService;
    private final BookRepository bookRepository;

    public BookController(BookService bookService, BookRepository bookRepository) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
    }

    @PutMapping("/{id}/borrow")
    public ResponseEntity<String> borrowBook(@PathVariable("id") Long id, 
                                            @RequestParam("studentName") String studentName, 
                                            HttpServletRequest request) {
        logger.info("Attempting to borrow book with id: {} by student: {}", id, studentName);
        
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            logger.warn("Invalid or missing Authorization header for book id: {}", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        String result = bookService.borrowBook(id, studentName, authHeader);
        if ("Student does not exist!".equals(result)) {
            logger.warn("Student {} not found for book id: {}", studentName, id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        } else if ("Book not found!".equals(result)) {
            logger.warn("Book with id: {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        } else if ("Book is already borrowed.".equals(result)) {
            logger.info("Book with id: {} is already borrowed", id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        logger.info("Book with id: {} borrowed successfully by {}", id, studentName);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable("id") Long id) {
        logger.debug("Fetching book with id: {}", id);
        Optional<Book> bookOpt = bookRepository.findById(id);
        return bookOpt.map(book -> {
            logger.debug("Book found: {}", book.getTitle());
            return ResponseEntity.ok(book);
        }).orElseGet(() -> {
            logger.warn("Book with id: {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        });
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Book addBook(@RequestBody Book book) {
        return bookService.addBook(book.getTitle(), book.getAuthor());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateBook(@PathVariable("id") Long id, @RequestBody Book updatedBook) {
        return ResponseEntity.ok(bookService.updateBook(id, updatedBook.getTitle(), updatedBook.getAuthor()));
    }

    @PutMapping("/{id}/return")
    public String returnBook(@PathVariable("id") Long id) {
        return bookService.returnBook(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> removeBook(@PathVariable("id") Long id) {
        return ResponseEntity.ok(bookService.removeBook(id));
    }
}