package com.dung.library.book.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import com.dung.library.book.model.Book;
import com.dung.library.book.model.StudentDTO;
import com.dung.library.book.repository.BookRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {
    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);
    private final BookRepository bookRepository;
    private final WebClient webClient;
    private static final String USER_SERVICE_PATH = "/api/students/name/";

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, WebClient webClient) {
        this.bookRepository = bookRepository;
        this.webClient = webClient;
    }

    @Override
    public String borrowBook(Long bookId, String studentName, String authHeader) {
        logger.info("Processing borrow request for bookId: {} by student: {}", bookId, studentName);
        
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isEmpty()) {
            logger.error("Book not found for id: {}", bookId);
            return "Book not found!";
        }

        Book book = bookOpt.get();
        if (book.isBorrowed()) {
            logger.info("Book {} is already borrowed", book.getTitle());
            return "Book is already borrowed.";
        }

        try {
            logger.debug("Calling UserMicroservice for student: {}", studentName);
            StudentDTO student = webClient.get()
                .uri(USER_SERVICE_PATH + studentName)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .header(HttpHeaders.ACCEPT, "application/json")
                .retrieve()
                .bodyToMono(StudentDTO.class)
                .doOnNext(response -> logger.debug("Received response for student: {}", response.getName()))
                .block();

            if (student == null) {
                logger.warn("Student {} not found in UserMicroservice", studentName);
                return "Student does not exist!";
            }
            
            book.setBorrowerId(student.getId());
            bookRepository.save(book);
            logger.info("Book {} successfully borrowed by student {}", book.getTitle(), studentName);
            return "Book borrowed successfully!";
        } catch (WebClientResponseException e) {
            logger.error("UserMicroservice error - Status: {}, Body: {}", 
                e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return "Student does not exist!";
            }
            return "Error contacting UserMicroservice: " + e.getMessage();
        } catch (Exception e) {
            logger.error("Unexpected error while borrowing book: {}", e.getMessage(), e);
            return "Error contacting UserMicroservice: " + e.getMessage();
        }
    }

    @Override
    public List<Book> getAllBooks() {
        logger.info("Fetching all books");
        return bookRepository.findAll();
    }

    @Override
    public Book addBook(String title, String author) {
        logger.info("Adding new book: {} by {}", title, author);
        Book book = new Book(title, author);
        return bookRepository.save(book);
    }

    @Override
    public String returnBook(Long bookId) {
        logger.info("Processing return request for bookId: {}", bookId);
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isEmpty()) {
            logger.error("Book not found for id: {}", bookId);
            return "Book not found!";
        }
        Book book = bookOpt.get();
        if (!book.isBorrowed()) {
            logger.warn("Attempt to return a book that was not borrowed: {}", book.getTitle());
            return "This book was not borrowed.";
        }
        book.setBorrowerId(null);
        bookRepository.save(book);
        logger.info("Book {} successfully returned", book.getTitle());
        return "Book returned successfully!";
    }

    @Override
    public String removeBook(Long bookId) {
        logger.info("Processing removal of bookId: {}", bookId);
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isEmpty()) {
            logger.error("Book not found for id: {}", bookId);
            return "Book not found!";
        }
        Book book = bookOpt.get();
        if (book.isBorrowed()) {
            logger.warn("Attempt to remove a borrowed book: {}", book.getTitle());
            return "Cannot remove a borrowed book!";
        }
        bookRepository.deleteById(bookId);
        logger.info("Book {} successfully removed", book.getTitle());
        return "Book removed successfully!";
    }

    @Override
    public List<Book> searchBooks(String title) {
        logger.info("Searching for books with title containing: {}", title);
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public String updateBook(Long bookId, String title, String author) {
        logger.info("Updating bookId: {} with new title: {} and author: {}", bookId, title, author);
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isEmpty()) {
            logger.error("Book not found for id: {}", bookId);
            return "Book not found!";
        }
        Book book = bookOpt.get();
        book.setTitle(title);
        book.setAuthor(author);
        bookRepository.save(book);
        logger.info("Book {} successfully updated", book.getTitle());
        return "Book updated successfully!";
    }
}
