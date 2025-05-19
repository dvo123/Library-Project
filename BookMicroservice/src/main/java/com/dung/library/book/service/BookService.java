package com.dung.library.book.service;

import com.dung.library.book.model.Book;
import java.util.List;

/**
 * Service interface for managing book-related operations in the library system.
 * Provides methods to borrow, return, add, remove, search, and update books.
 */
public interface BookService {

    /**
     * Borrows a book for a specified student.
     *
     * @param bookId      the ID of the book to borrow
     * @param studentName the name of the student borrowing the book
     * @param authHeader  the HTTP Authorization header for authentication
     * @return a message indicating the result (e.g., "Book borrowed successfully!" or an error)
     */
    String borrowBook(Long bookId, String studentName, String authHeader);

    /**
     * Retrieves all books in the library.
     *
     * @return a list of all books
     */
    List<Book> getAllBooks();

    /**
     * Adds a new book to the library.
     *
     * @param title  the title of the book
     * @param author the author of the book
     * @return the newly added book
     */
    Book addBook(String title, String author);

    /**
     * Returns a borrowed book to the library.
     *
     * @param bookId the ID of the book to return
     * @return a message indicating the result (e.g., "Book returned successfully!" or an error)
     */
    String returnBook(Long bookId);

    /**
     * Removes a book from the library.
     *
     * @param bookId the ID of the book to remove
     * @return a message indicating the result (e.g., "Book removed successfully!" or an error)
     */
    String removeBook(Long bookId);

    /**
     * Searches for books by title (case-insensitive).
     *
     * @param title the title or partial title to search for
     * @return a list of books matching the title
     */
    List<Book> searchBooks(String title);

    /**
     * Updates an existing book’s title and author.
     *
     * @param bookId the ID of the book to update
     * @param title  the new title
     * @param author the new author
     * @return a message indicating the result (e.g., "Book updated successfully!" or an error)
     */
    String updateBook(Long bookId, String title, String author);
}