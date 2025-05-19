package com.dung.library.UserMicroservice;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.dung.library.service.StudentService;
import com.dung.library.user.model.Student; // Explicitly import the correct Student class
import com.dung.library.user.repository.StudentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StudentServiceTest {

//    private StudentService studentService;
//
//    @Mock
//    private StudentRepository studentRepository;
//
//    @InjectMocks
//    private StudentService studentServiceUnderTest;
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//        studentService = studentServiceUnderTest; // Assign the injected instance
//    }
//
//    @Test
//    void testGetAllStudents() {
//        // Arrange
//        List<com.dung.library.user.model.Student> students = Arrays.asList(
//            new com.dung.library.user.model.Student("Khang Nguyen", "khang123@gmail.com"),
//            new com.dung.library.user.model.Student("Dung Vo", "dungvo98@gmail.com")
//        );
//        when(studentRepository.findAll()).thenReturn(students);
//
//        // Act
//        List<com.dung.library.user.model.Student> result = studentService.getAllStudents();
//
//        // Assert
//        assertEquals(2, result.size());
//        assertEquals("Khang Nguyen", result.get(0).getName());
//        assertEquals("Dung Vo", result.get(1).getName());
//        verify(studentRepository, times(1)).findAll();
//    }
//
//    @Test
//    void testAddStudent() {
//        // Arrange
//        com.dung.library.user.model.Student student = new com.dung.library.user.model.Student("New Student", "new@mail.com");
//        student.setId(1L); // Simulate ID generation
//        when(studentRepository.save(any(com.dung.library.user.model.Student.class))).thenReturn(student);
//
//        // Act
//        com.dung.library.user.model.Student result = studentService.addStudent("New Student", "new@mail.com");
//
//        // Assert
//        assertEquals("New Student", result.getName());
//        assertEquals("new@mail.com", result.getEmail());
//        verify(studentRepository, times(1)).save(any(com.dung.library.user.model.Student.class));
//    }
//
//    @Test
//    void testRemoveStudent_Success() {
//        // Arrange
//        com.dung.library.user.model.Student student = new com.dung.library.user.model.Student("Khang Nguyen", "khang123@gmail.com");
//        student.setId(1L);
//        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
//
//        // Act
//        String result = studentService.removeStudent(1L);
//
//        // Assert
//        assertEquals("Student removed successfully!", result);
//        verify(studentRepository, times(1)).deleteById(1L);
//    }
//
//    @Test
//    void testRemoveStudent_NotFound() {
//        // Arrange
//        when(studentRepository.findById(999L)).thenReturn(Optional.empty());
//
//        // Act
//        String result = studentService.removeStudent(999L);
//
//        // Assert
//        assertEquals("Student not found!", result);
//        verify(studentRepository, never()).deleteById(anyLong());
//    }
}