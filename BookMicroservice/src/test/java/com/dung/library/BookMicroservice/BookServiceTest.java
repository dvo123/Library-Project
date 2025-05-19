package com.dung.library.BookMicroservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.dung.library.book.model.Book;
import com.dung.library.book.model.StudentDTO;
import com.dung.library.book.repository.BookRepository;
import com.dung.library.book.service.BookService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testBorrowBook_Success() {
        // Arrange
        Long bookId = 1L;
        String studentName = "Khang Nguyen";
        String authHeader = "Basic YWRtaW46YXNkZg=="; // admin:asdf
        Book book = new Book("Test Book", "Test Author");
        book.setId(bookId);
        StudentDTO studentDTO = new StudentDTO(2L, "Khang Nguyen", "khang123@gmail.com");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(restTemplate.exchange(
            eq("http://localhost:8081/api/students/name/" + studentName),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(StudentDTO.class)
        )).thenReturn(new ResponseEntity<>(studentDTO, HttpStatus.OK));

        // Act
        String result = bookService.borrowBook(bookId, studentName, authHeader);

        // Assert
        assertEquals("Book borrowed successfully!", result);
        assertEquals(2L, book.getBorrowerId());
    }

    @Test
    public void testBorrowBook_BookNotFound() {
        // Arrange
        Long bookId = 999L;
        String studentName = "Khang Nguyen";
        String authHeader = "Basic YWRtaW46YXNkZg==";

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Act
        String result = bookService.borrowBook(bookId, studentName, authHeader);

        // Assert
        assertEquals("Book not found!", result);
    }

    @Test
    public void testBorrowBook_StudentNotFound() {
        // Arrange
        Long bookId = 1L;
        String studentName = "NonExistentStudent";
        String authHeader = "Basic YWRtaW46YXNkZg==";
        Book book = new Book("Test Book", "Test Author");
        book.setId(bookId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(restTemplate.exchange(
            eq("http://localhost:8081/api/students/name/" + studentName),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(StudentDTO.class)
        )).thenThrow(new org.springframework.web.client.HttpClientErrorException(HttpStatus.NOT_FOUND));

        // Act
        String result = bookService.borrowBook(bookId, studentName, authHeader);

        // Assert
        assertEquals("Student does not exist!", result);
    }

    @Test
    public void testBorrowBook_AlreadyBorrowed() {
        // Arrange
        Long bookId = 1L;
        String studentName = "Khang Nguyen";
        String authHeader = "Basic YWRtaW46YXNkZg==";
        Book book = new Book("Test Book", "Test Author");
        book.setId(bookId);
        book.setBorrowed(true); // Already borrowed

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // Act
        String result = bookService.borrowBook(bookId, studentName, authHeader);

        // Assert
        assertEquals("Book is already borrowed.", result);
    }
}