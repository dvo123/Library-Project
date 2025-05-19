package com.dung.library.BookMicroservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.dung.library.book.service.BookService;
import com.dung.library.book.controller.BookController;
import com.dung.library.book.repository.BookRepository;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private BookRepository bookRepository;

    @BeforeEach
    public void setUp() {}

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testBorrowBook_Success() throws Exception {
        when(bookService.borrowBook(1L, "Khang Nguyen", "Basic YWRtaW46YXNkZg=="))
            .thenReturn("Book borrowed successfully!");

        mockMvc.perform(put("/api/books/1/borrow")
                .param("studentName", "Khang Nguyen")
                .header("Authorization", "Basic YWRtaW46YXNkZg==")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("Book borrowed successfully!"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testBorrowBook_StudentNotFound() throws Exception {
        when(bookService.borrowBook(1L, "NonExistentStudent", "Basic YWRtaW46YXNkZg=="))
            .thenReturn("Student does not exist!");

        mockMvc.perform(put("/api/books/1/borrow")
                .param("studentName", "NonExistentStudent")
                .header("Authorization", "Basic YWRtaW46YXNkZg==")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Student does not exist!"));
    }
}