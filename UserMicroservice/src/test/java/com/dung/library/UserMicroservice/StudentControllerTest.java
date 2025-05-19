package com.dung.library.UserMicroservice;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.dung.library.user.controller.StudentController;
import com.dung.library.user.model.Student;
import com.dung.library.user.service.StudentService;

import java.util.Arrays;
import java.util.Optional;

public class StudentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();
    }

    @Test
    void testGetAllStudents() throws Exception {
        // Arrange
        Student student1 = new Student("Khang Nguyen", "khang123@gmail.com");
        Student student2 = new Student("Dung Vo", "dungvo98@gmail.com");
        when(studentService.getAllStudents()).thenReturn(Arrays.asList(student1, student2));

        // Act & Assert
        mockMvc.perform(get("/api/students")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(2))
            .andExpect(jsonPath("$[0].name").value("Khang Nguyen"))
            .andExpect(jsonPath("$[1].name").value("Dung Vo"));

        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void testGetStudentByName_Success() throws Exception {
        // Arrange
        Student student = new Student("Khang Nguyen", "khang123@gmail.com");
        when(studentService.getStudentByName("Khang Nguyen")).thenReturn(Optional.of(student));

        // Act & Assert
        mockMvc.perform(get("/api/students/name/Khang%20Nguyen")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Khang Nguyen"))
            .andExpect(jsonPath("$.email").value("khang123@gmail.com"));

        verify(studentService, times(1)).getStudentByName("Khang Nguyen");
    }

    @Test
    void testGetStudentByName_NotFound() throws Exception {
        // Arrange
        when(studentService.getStudentByName("NonExistentStudent")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/students/name/NonExistentStudent")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        verify(studentService, times(1)).getStudentByName("NonExistentStudent");
    }

    @Test
    void testAddStudent() throws Exception {
        // Arrange
        Student student = new Student("New Student", "new@mail.com");
        when(studentService.addStudent("New Student", "new@mail.com")).thenReturn(student);

        // Act & Assert
        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"New Student\", \"email\": \"new@mail.com\"}"))
            .andExpect(status().isOk())
            .andExpect(content().string("Student added successfully!"));

        verify(studentService, times(1)).addStudent("New Student", "new@mail.com");
    }
}