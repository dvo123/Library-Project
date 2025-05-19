package com.dung.library.console;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import com.dung.library.model.Student;

import java.util.Arrays;
import java.util.Base64;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * A console-based interface for interacting with the library management system.
 * This class provides a menu-driven application that allows users to manage books and students
 * by making HTTP requests to {@code BookMicroservice} and {@code UserMicroservice} using {@link WebClient}.
 * Supports operations such as displaying, adding, borrowing, returning, searching, updating, and removing
 * books and students, with role-based access control for admin-only actions.
 *
 * @author [Dung Vo]
 * @version 1.0
 * @since 2025-02-27
 */
@Component
public class LibraryConsole {
    private final WebClient webClient;
    private final PasswordEncoder passwordEncoder;
    private final Scanner scanner = new Scanner(System.in);
    private String currentRole;
    private String currentUsername;
    private String currentPassword;

    /**
     * Constructs a new {@code LibraryConsole} instance with the specified {@link WebClient} and
     * {@link PasswordEncoder} for making HTTP requests and handling password verification.
     *
     * @param webClient       the {@link WebClient} used to communicate with microservices
     * @param passwordEncoder the {@link PasswordEncoder} for validating user credentials
     */
    @Autowired
    public LibraryConsole(WebClient webClient, PasswordEncoder passwordEncoder) {
        this.webClient = webClient;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Initiates the console application by prompting the user to log in and then entering
     * an interactive menu loop. The loop continues until the user selects the exit option (12).
     * Handles invalid menu input gracefully, allowing the user to retry.
     */
    public void start() {
        login();

        while (true) {
            printMenu();
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> displayStudents();
                    case 2 -> { if (isAdmin()) createStudent(); else accessDenied(); }
                    case 3 -> displayBooks();
                    case 4 -> { if (isAdmin()) addBook(); else accessDenied(); }
                    case 5 -> borrowBook();
                    case 6 -> returnBook();
                    case 7 -> searchBook();
                    case 8 -> { if (isAdmin()) removeBook(); else accessDenied(); }
                    case 9 -> { if (isAdmin()) removeStudent(); else accessDenied(); }
                    case 10 -> { if (isAdmin()) updateBook(); else accessDenied(); }
                    case 11 -> { if (isAdmin()) updateStudent(); else accessDenied(); }
                    case 12 -> { System.out.println("Exiting system..."); return; }
                    default -> System.out.println("Invalid choice! Please enter a number between 1 and 12.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number between 1 and 12.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    /**
     * Prompts the user to log in by entering a username and password. Validates credentials against
     * predefined hashes for "admin" (password: "asdf") and "user" (password: "1234"). Sets the
     * {@code currentRole}, {@code currentUsername}, and {@code currentPassword} fields upon successful login.
     * Continues prompting until valid credentials are provided.
     */
    private void login() {
        while (currentRole == null) {
            System.out.println("\n===== Login =====");
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            String adminHash = "$2a$10$Abbw8uvfvLIKGCVqrPqIdefEGZbtHOco0//E3uE1ZvlfwWf/t2BDi"; // Hashed "asdf"
            String userHash = passwordEncoder.encode("1234");

            if (username.equals("admin") && passwordEncoder.matches(password, adminHash)) {
                currentRole = "ADMIN";
                currentUsername = username;
                currentPassword = password;
                System.out.println("Stored password hash: " + adminHash);
                System.out.println("Logged in as " + currentRole);
            } else if (username.equals("user") && passwordEncoder.matches(password, userHash)) {
                currentRole = "USER";
                currentUsername = username;
                currentPassword = password;
                System.out.println("Stored password hash: " + userHash);
                System.out.println("Logged in as " + currentRole);
            } else {
                System.out.println("Invalid username or password! Try again.");
            }
        }
    }

    /**
     * Displays the main menu options available to the user, tailored to the current role (ADMIN or USER).
     * Options include displaying books/students, adding, borrowing, returning, searching, updating,
     * and removing, with admin-only operations clearly marked.
     */
    private void printMenu() {
        System.out.println("\n===== Library Management System =====");
        System.out.println("Current Role: " + currentRole);
        System.out.println("1. Display Students");
        System.out.println("2. Create Student (Admin Only)");
        System.out.println("3. Display Books");
        System.out.println("4. Add Book (Admin Only)");
        System.out.println("5. Borrow Book");
        System.out.println("6. Return Book");
        System.out.println("7. Search Book");
        System.out.println("8. Remove Book (Admin Only)");
        System.out.println("9. Remove Student (Admin Only)");
        System.out.println("10. Update Book (Admin Only)");
        System.out.println("11. Update Student (Admin Only)");
        System.out.println("12. Exit");
        System.out.print("Choose an option: ");
    }

    /**
     * Checks if the current user has the ADMIN role.
     *
     * @return {@code true} if the current role is "ADMIN", {@code false} otherwise
     */
    private boolean isAdmin() {
        return "ADMIN".equals(currentRole);
    }

    /**
     * Displays an access denied message to the console when a non-admin user attempts an admin-only operation.
     */
    private void accessDenied() {
        System.out.println("Admin access required! Please log in as admin.");
    }

    /**
     * Retrieves and displays all students from {@code UserMicroservice} at {@code http://localhost:8081/api/students}.
     * Prints each student’s ID, name, and email to the console. If no students are found or an error occurs,
     * an appropriate message is displayed.
     */
    private void displayStudents() {
        try {
            Student[] students = webClient.get()
                .uri("http://localhost:8081/api/students")
                .headers(headers -> headers.addAll(createAuthHeaders()))
                .retrieve()
                .bodyToMono(Student[].class)
                .block();
            List<Student> studentList = Arrays.asList(students != null ? students : new Student[0]);
            if (studentList.isEmpty()) {
                System.out.println("No students found.");
            } else {
                studentList.forEach(s -> System.out.println("ID: " + s.getId() + ", Name: " + s.getName() + ", Email: " + s.getEmail()));
            }
        } catch (Exception e) {
            System.out.println("Error fetching students: " + e.getMessage());
        }
    }

    /**
     * Prompts the user to enter a name and email, then creates a new student by sending a POST request
     * to {@code UserMicroservice} at {@code http://localhost:8081/api/students}. Displays the response
     * or an error message if the request fails.
     */
    private void createStudent() {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();
        System.out.print("Enter student email: ");
        String email = scanner.nextLine();
        try {
            String response = webClient.post()
                .uri("http://localhost:8081/api/students")
                .headers(headers -> headers.addAll(createAuthHeaders()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"" + name + "\",\"email\":\"" + email + "\"}")
                .retrieve()
                .bodyToMono(String.class)
                .block();
            System.out.println(response);
        } catch (Exception e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

    /**
     * Retrieves and displays all books from {@code BookMicroservice} at {@code http://localhost:8080/api/books}.
     * Prints each book’s ID, title, author, and borrowing status to the console. If no books are found
     * or an error occurs, an appropriate message is displayed.
     */
    private void displayBooks() {
        try {
            Book[] books = webClient.get()
                .uri("http://localhost:8080/api/books")
                .headers(headers -> headers.addAll(createAuthHeaders()))
                .retrieve()
                .bodyToMono(Book[].class)
                .block();
            List<Book> bookList = Arrays.asList(books != null ? books : new Book[0]);
            if (bookList.isEmpty()) {
                System.out.println("No books found.");
            } else {
                for (Book book : bookList) {
                    String status = book.isBorrowed() ? "(Borrowed)" : "(Available)";
                    System.out.println("ID: " + book.getId() + ", Title: " + book.getTitle() + ", Author: " + book.getAuthor() + " " + status);
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching books: " + e.getMessage());
        }
    }

    /**
     * Prompts the user to enter a book title and author, then adds a new book by sending a POST request
     * to {@code BookMicroservice} at {@code http://localhost:8080/api/books}. Displays the added book’s
     * title or an error message if the request fails.
     */
    private void addBook() {
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();
        System.out.print("Enter book author: ");
        String author = scanner.nextLine();
        try {
            Book response = webClient.post()
                .uri("http://localhost:8080/api/books")
                .headers(headers -> headers.addAll(createAuthHeaders()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"title\":\"" + title + "\",\"author\":\"" + author + "\"}")
                .retrieve()
                .bodyToMono(Book.class)
                .block();
            System.out.println("Book added: " + response.getTitle());
        } catch (Exception e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    /**
     * Prompts the user for a book ID and student name, then attempts to borrow the book by sending a PUT request
     * to {@code BookMicroservice} at {@code http://localhost:8080/api/books/{bookId}/borrow}. Retries on invalid
     * book ID input and handles specific error cases for non-existent students or books.
     */
    private void borrowBook() {
        Long bookId = null;
        while (bookId == null) {
            System.out.print("Enter book ID: ");
            try {
                bookId = scanner.nextLong();
                scanner.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid book ID! Please enter a valid number.");
                scanner.nextLine(); // Clear invalid input
            }
        }

        System.out.print("Enter student name: ");
        String studentName = scanner.nextLine();

        try {
            String response = webClient.put()
                .uri("http://localhost:8080/api/books/" + bookId + "/borrow?studentName=" + studentName)
                .headers(headers -> headers.addAll(createAuthHeaders()))
                .retrieve()
                .bodyToMono(String.class)
                .block();
            System.out.println(response);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                String responseBody = e.getResponseBodyAsString();
                if (responseBody.contains("Student")) {
                    System.out.println("Student does not exist!");
                } else {
                    System.out.println("Book not found!");
                }
            } else {
                System.out.println("Error: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Prompts the user for a book ID, then returns the book by sending a PUT request to
     * {@code BookMicroservice} at {@code http://localhost:8080/api/books/{bookId}/return}.
     * Retries on invalid book ID input and displays the response or an error message.
     */
    private void returnBook() {
        Long bookId = null;
        while (bookId == null) {
            System.out.print("Enter book ID: ");
            try {
                bookId = scanner.nextLong();
                scanner.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid book ID! Please enter a valid number.");
                scanner.nextLine(); // Clear invalid input
            }
        }

        try {
            String response = webClient.put()
                .uri("http://localhost:8080/api/books/" + bookId + "/return")
                .headers(headers -> headers.addAll(createAuthHeaders()))
                .retrieve()
                .bodyToMono(String.class)
                .block();
            System.out.println(response);
        } catch (Exception e) {
            System.out.println("Error returning book: " + e.getMessage());
        }
    }

    /**
     * Prompts the user for a book title, fetches all books from {@code BookMicroservice} at
     * {@code http://localhost:8080/api/books}, and filters them locally by title (case-insensitive).
     * Displays matching books or an error message if the request fails.
     */
    private void searchBook() {
        System.out.print("Enter book title to search: ");
        String title = scanner.nextLine();
        try {
            Book[] books = webClient.get()
                .uri("http://localhost:8080/api/books")
                .headers(headers -> headers.addAll(createAuthHeaders()))
                .retrieve()
                .bodyToMono(Book[].class)
                .block();
            List<Book> bookList = Arrays.asList(books != null ? books : new Book[0]);
            bookList.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(title.toLowerCase()))
                .forEach(b -> System.out.println("ID: " + b.getId() + ", Title: " + b.getTitle() + ", Author: " + b.getAuthor()));
        } catch (Exception e) {
            System.out.println("Error searching books: " + e.getMessage());
        }
    }

    /**
     * Prompts the user for a book ID, then removes the book by sending a DELETE request to
     * {@code BookMicroservice} at {@code http://localhost:8080/api/books/{bookId}}.
     * Retries on invalid book ID input and displays the response or an error message.
     */
    private void removeBook() {
        Long bookId = null;
        while (bookId == null) {
            System.out.print("Enter book ID: ");
            try {
                bookId = scanner.nextLong();
                scanner.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid book ID! Please enter a valid number.");
                scanner.nextLine(); // Clear invalid input
            }
        }

        try {
            String response = webClient.delete()
                .uri("http://localhost:8080/api/books/" + bookId)
                .headers(headers -> headers.addAll(createAuthHeaders()))
                .retrieve()
                .bodyToMono(String.class)
                .block();
            System.out.println(response);
        } catch (Exception e) {
            System.out.println("Error removing book: " + e.getMessage());
        }
    }

    /**
     * Prompts the user for a student ID, then removes the student by sending a DELETE request to
     * {@code UserMicroservice} at {@code http://localhost:8081/api/students/{studentId}}.
     * Prevents removal if the student has borrowed books by checking {@code BookMicroservice}.
     * Retries on invalid student ID input and displays the response or an error message.
     */
    private void removeStudent() {
        Long studentId = null;
        while (studentId == null) {
            System.out.print("Enter student ID: ");
            try {
                studentId = scanner.nextLong();
                scanner.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid student ID! Please enter a valid number.");
                scanner.nextLine(); // Clear invalid input
            }
        }

        final Long finalStudentId = studentId;

        try {
            Book[] books = webClient.get()
                .uri("http://localhost:8080/api/books")
                .headers(headers -> headers.addAll(createAuthHeaders()))
                .retrieve()
                .bodyToMono(Book[].class)
                .block();
            List<Book> bookList = Arrays.asList(books != null ? books : new Book[0]);
            boolean hasBorrowed = bookList.stream().anyMatch(b -> b.isBorrowed() && finalStudentId.equals(b.getBorrowerId()));
            if (hasBorrowed) {
                System.out.println("Cannot remove student: They have borrowed books!");
                return;
            }
        } catch (Exception e) {
            System.out.println("Error checking borrowed books: " + e.getMessage());
            return;
        }

        try {
            String response = webClient.delete()
                .uri("http://localhost:8081/api/students/" + finalStudentId)
                .headers(headers -> headers.addAll(createAuthHeaders()))
                .retrieve()
                .bodyToMono(String.class)
                .block();
            System.out.println(response);
        } catch (Exception e) {
            System.out.println("Error removing student: " + e.getMessage());
        }
    }

    /**
     * Prompts the user for a book ID, new title, and new author, then updates the book by sending a PUT request
     * to {@code BookMicroservice} at {@code http://localhost:8080/api/books/{bookId}}.
     * Retries on invalid book ID input and displays the response or an error message.
     */
    private void updateBook() {
        Long bookId = null;
        while (bookId == null) {
            System.out.print("Enter book ID: ");
            try {
                bookId = scanner.nextLong();
                scanner.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid book ID! Please enter a valid number.");
                scanner.nextLine(); // Clear invalid input
            }
        }

        System.out.print("Enter new title: ");
        String title = scanner.nextLine();
        System.out.print("Enter new author: ");
        String author = scanner.nextLine();
        try {
            String response = webClient.put()
                .uri("http://localhost:8080/api/books/" + bookId)
                .headers(headers -> headers.addAll(createAuthHeaders()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"title\":\"" + title + "\",\"author\":\"" + author + "\"}")
                .retrieve()
                .bodyToMono(String.class)
                .block();
            System.out.println(response);
        } catch (Exception e) {
            System.out.println("Error updating book: " + e.getMessage());
        }
    }

    /**
     * Prompts the user for a student ID, new name, and new email, then updates the student by sending a PUT request
     * to {@code UserMicroservice} at {@code http://localhost:8081/api/students/{studentId}}.
     * Retries on invalid student ID input and displays the response or an error message.
     */
    private void updateStudent() {
        Long studentId = null;
        while (studentId == null) {
            System.out.print("Enter student ID: ");
            try {
                studentId = scanner.nextLong();
                scanner.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid student ID! Please enter a valid number.");
                scanner.nextLine(); // Clear invalid input
            }
        }

        System.out.print("Enter new name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new email: ");
        String email = scanner.nextLine();
        try {
            String response = webClient.put()
                .uri("http://localhost:8081/api/students/" + studentId)
                .headers(headers -> headers.addAll(createAuthHeaders()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"" + name + "\",\"email\":\"" + email + "\"}")
                .retrieve()
                .bodyToMono(String.class)
                .block();
            System.out.println(response);
        } catch (Exception e) {
            System.out.println("Error updating student: " + e.getMessage());
        }
    }

    /**
     * Creates HTTP headers with Basic Authentication using the current username and password.
     * Sets the Authorization header with Base64-encoded credentials and the Content-Type to JSON.
     *
     * @return a configured {@code HttpHeaders} object for use in HTTP requests
     */
    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String auth = currentUsername + ":" + currentPassword;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.add("Authorization", "Basic " + encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}