package com.dung.library.user.service;

import com.dung.library.user.model.Student;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing student-related operations in the library system.
 * Provides methods to retrieve, add, remove, and update students.
 */
public interface StudentService {

    /**
     * Retrieves a student by their name.
     *
     * @param name the name of the student
     * @return an {@code Optional} containing the student if found, or empty if not
     */
    Optional<Student> getStudentByName(String name);

    /**
     * Retrieves a student by their ID.
     *
     * @param id the ID of the student
     * @return an {@code Optional} containing the student if found, or empty if not
     */
    Optional<Student> getStudentById(Long id);

    /**
     * Retrieves all students in the library system.
     *
     * @return a list of all students
     */
    List<Student> getAllStudents();

    /**
     * Adds a new student to the library system.
     *
     * @param name  the name of the student
     * @param email the email of the student
     * @return the newly added student
     */
    Student addStudent(String name, String email);

    /**
     * Removes a student from the library system.
     *
     * @param studentId the ID of the student to remove
     * @return a message indicating the result (e.g., "Student removed successfully!" or an error)
     */
    String removeStudent(Long studentId);

    /**
     * Updates an existing student’s name and email.
     *
     * @param studentId the ID of the student to update
     * @param name      the new name
     * @param email     the new email
     * @return a message indicating the result (e.g., "Student updated successfully!" or an error)
     */
    String updateStudent(Long studentId, String name, String email);
} //@lomnok thu vien