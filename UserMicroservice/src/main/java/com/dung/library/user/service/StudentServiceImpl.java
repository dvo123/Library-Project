package com.dung.library.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.dung.library.user.model.Student;
import com.dung.library.user.repository.StudentRepository;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link StudentService} for managing student operations.
 * Uses a {@link StudentRepository} for persistence.
 */
@Service
public class StudentServiceImpl implements StudentService {
    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);
    private final StudentRepository studentRepository;

    /**
     * Constructs a new {@code StudentServiceImpl} with the specified repository.
     *
     * @param studentRepository the repository for student data access
     */
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        logger.debug("StudentServiceImpl initialized with repository");
    }

    @Override
    public Optional<Student> getStudentByName(String name) {
        logger.info("Fetching student by name: {}", name);
        Optional<Student> studentOpt = studentRepository.findByName(name);
        if (studentOpt.isPresent()) {
            logger.debug("Found student with name: {}", name);
        } else {
            logger.warn("No student found with name: {}", name);
        }
        return studentOpt;
    }

    @Override
    public Optional<Student> getStudentById(Long id) {
        logger.info("Fetching student by id: {}", id);
        Optional<Student> studentOpt = studentRepository.findById(id);
        if (studentOpt.isPresent()) {
            logger.debug("Found student with id: {}", id);
        } else {
            logger.warn("No student found with id: {}", id);
        }
        return studentOpt;
    }

    @Override
    public List<Student> getAllStudents() {
        logger.info("Fetching all students");
        List<Student> students = studentRepository.findAll();
        logger.debug("Retrieved {} students", students.size());
        return students;
    }

    @Override
    public Student addStudent(String name, String email) {
        logger.info("Adding new student: {}", name);
        try {
            Student student = new Student(name, email);
            Student savedStudent = studentRepository.save(student);
            logger.debug("Student {} added successfully with id: {}", name, savedStudent.getId());
            return savedStudent;
        } catch (Exception e) {
            logger.error("Error adding student {}: {}", name, e.getMessage(), e);
            throw new RuntimeException("Failed to add student: " + e.getMessage(), e);
        }
    }

    @Override
    public String removeStudent(Long studentId) {
        logger.info("Attempting to remove student with id: {}", studentId);
        try {
            Optional<Student> studentOpt = studentRepository.findById(studentId);
            if (studentOpt.isEmpty()) {
                logger.warn("Student with id: {} not found for removal", studentId);
                return "Student not found!";
            }
            studentRepository.deleteById(studentId);
            logger.debug("Student with id: {} removed successfully", studentId);
            return "Student removed successfully!";
        } catch (Exception e) {
            logger.error("Error removing student with id {}: {}", studentId, e.getMessage(), e);
            throw new RuntimeException("Failed to remove student: " + e.getMessage(), e);
        }
    }

    @Override
    public String updateStudent(Long studentId, String name, String email) {
        logger.info("Updating student with id: {}", studentId);
        try {
            Optional<Student> studentOpt = studentRepository.findById(studentId);
            if (studentOpt.isEmpty()) {
                logger.warn("Student with id: {} not found for update", studentId);
                return "Student not found!";
            }
            Student student = studentOpt.get();
            student.setName(name);
            student.setEmail(email);
            studentRepository.save(student);
            logger.debug("Student with id: {} updated successfully", studentId);
            return "Student updated successfully!";
        } catch (Exception e) {
            logger.error("Error updating student with id {}: {}", studentId, e.getMessage(), e);
            throw new RuntimeException("Failed to update student: " + e.getMessage(), e);
        }
    }
}