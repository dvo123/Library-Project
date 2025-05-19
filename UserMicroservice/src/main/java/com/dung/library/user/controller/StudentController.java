package com.dung.library.user.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;

import com.dung.library.user.model.Student;
import com.dung.library.user.service.StudentService;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
        logger.debug("StudentController initialized");
    }

    @GetMapping
    public List<Student> getAllStudents() {
        logger.info("Fetching all students");
        List<Student> students = studentService.getAllStudents();
        logger.debug("Retrieved {} students", students.size());
        return students;
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Student> getStudentByName(@PathVariable("name") String name) {
        logger.info("Fetching student by name: {}", name);
        Optional<Student> studentOpt = studentService.getStudentByName(name);
        return studentOpt.map(student -> {
            logger.debug("Found student: {} with name: {}", student.getId(), name);
            return ResponseEntity.ok(student);
        }).orElseGet(() -> {
            logger.warn("No student found with name: {}", name);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        });
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable("id") Long id) {
        logger.info("Fetching student by id: {}", id);
        Optional<Student> studentOpt = studentService.getStudentById(id);
        return studentOpt.map(student -> {
            logger.debug("Found student: {} with id: {}", student.getName(), id);
            return ResponseEntity.ok(student);
        }).orElseGet(() -> {
            logger.warn("No student found with id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        });
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addStudent(@RequestBody Student student) {
        logger.info("Adding new student: {}", student.getName());
        try {
            studentService.addStudent(student.getName(), student.getEmail());
            logger.debug("Student {} added successfully", student.getName());
            return ResponseEntity.ok("Student added successfully!");
        } catch (Exception e) {
            logger.error("Error adding student {}: {}", student.getName(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error adding student: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateStudent(@PathVariable("id") Long id, @RequestBody Student updatedStudent) {
        logger.info("Updating student with id: {}", id);
        try {
            String result = studentService.updateStudent(id, updatedStudent.getName(), updatedStudent.getEmail());
            logger.debug("Student with id: {} updated successfully", id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error updating student with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating student: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> removeStudent(@PathVariable("id") Long id) {
        logger.info("Removing student with id: {}", id);
        try {
            String result = studentService.removeStudent(id);
            logger.debug("Student with id: {} removed successfully", id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error removing student with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error removing student: " + e.getMessage());
        }
    }
}