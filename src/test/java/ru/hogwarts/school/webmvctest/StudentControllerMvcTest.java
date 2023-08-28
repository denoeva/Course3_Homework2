package ru.hogwarts.school.webmvctest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(StudentController.class)
public class StudentControllerMvcTest {
    @SpyBean
    StudentService studentService;
    @MockBean
    StudentRepository studentRepository;
    @MockBean
    FacultyRepository facultyRepository;
    @MockBean
    AvatarService avatarService;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getById() throws Exception {
        Student student = new Student(1L, "Олег", 20);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        mockMvc.perform(MockMvcRequestBuilders.get("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.id").value("1"))
                .andExpect((ResultMatcher) jsonPath("$.name").value("Олег"))
                .andExpect((ResultMatcher) jsonPath("$.age").value("20"));
    }

    @Test
    void create() throws Exception {
        Student student = new Student(1L, "Олег", 20);
        when(studentRepository.save(ArgumentMatchers.any(Student.class))).thenReturn(student);
        mockMvc.perform(post("/student")
                        .content(objectMapper.writeValueAsString(student))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.id").value("1"))
                .andExpect((ResultMatcher) jsonPath("$.name").value("Олег"))
                .andExpect((ResultMatcher) jsonPath("$.age").value("20"));
    }

    @Test
    void editStudent() throws Exception {
        Student student = new Student(1L, "Олег", 20);
        when(studentRepository.save(ArgumentMatchers.any(Student.class))).thenReturn(student);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        mockMvc.perform(put("/student/1")
//        mockMvc.perform(put("/student/" + student.getId())
                        .content(objectMapper.writeValueAsString(student))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
//                .andExpect((ResultMatcher) jsonPath("$.id").value("1"))
//                .andExpect((ResultMatcher) jsonPath("$.name").value("Олег"))
//                .andExpect((ResultMatcher) jsonPath("$.age").value("20"));
    }

    @Test
    void deleteStudent() throws Exception {
        Student student = new Student(1L, "Олег", 20);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
//        mockMvc.perform(delete("/student/1" + student.getId())
        mockMvc.perform(delete("/student/1")
                        .content(objectMapper.writeValueAsString(student))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
//                .andExpect((ResultMatcher) jsonPath("$.id").value("1"))
//                .andExpect((ResultMatcher) jsonPath("$.name").value("Олег"))
//                .andExpect((ResultMatcher) jsonPath("$.age").value("20"));
    }

    @Test
    void filteredBetween() throws Exception {
        when(studentRepository.findAllByAgeBetween(0, 20)).thenReturn(Arrays.asList(
                new Student(1l, "Олег", 20),
                new Student(2l, "Оксана", 21)
        ));
        mockMvc.perform(MockMvcRequestBuilders.get("/student/age-between?min=0&max=20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect((ResultMatcher) jsonPath("$").isArray())
                .andExpect((ResultMatcher) jsonPath("$[0].id").value(1L))
                .andExpect((ResultMatcher) jsonPath("$[1].id").value(2L));
    }

    @Test
    void getAll() throws Exception {
        when(studentRepository.findAll()).thenReturn(Arrays.asList(
                new Student(1l, "Олег", 64),
                new Student(2l, "Оксана", 32)
        ));
        mockMvc.perform(MockMvcRequestBuilders.get("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$").isArray())
                .andExpect((ResultMatcher) jsonPath("$[0].id").value(1L))
                .andExpect((ResultMatcher) jsonPath("$[1].id").value(2L));
    }

    @Test
    void filteredByAge() throws Exception {
        when(studentRepository.findAllByAge(28)).thenReturn(Arrays.asList(
                new Student(1l, "Олег", 28),
                new Student(2l, "Оксана", 32),
                new Student(3l, "Мария", 42),
                new Student(4l, "Степан", 13)
        ));
        mockMvc.perform(MockMvcRequestBuilders.get("/student/age?age=28")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect((ResultMatcher) jsonPath("$").isArray())
                .andExpect((ResultMatcher) jsonPath("$[0].id").value(1L));
    }
}
