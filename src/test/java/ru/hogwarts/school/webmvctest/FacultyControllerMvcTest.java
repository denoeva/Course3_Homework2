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
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(FacultyController.class)
public class FacultyControllerMvcTest {
    @SpyBean
    FacultyService facultyService;
    @MockBean
    StudentRepository studentRepository;
    @MockBean
    FacultyRepository facultyRepository;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getById() throws Exception {
        Faculty faculty = new Faculty(1L, "Медведи", "red");
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.id").value("1"))
                .andExpect((ResultMatcher) jsonPath("$.name").value("Медведи"))
                .andExpect((ResultMatcher) jsonPath("$.color").value("red"));
    }

    @Test
    void create() throws Exception {
        Faculty faculty = new Faculty(1L, "Медведи", "red");
        when(facultyRepository.save(ArgumentMatchers.any(Faculty.class))).thenReturn(faculty);
        mockMvc.perform(post("/faculty")
                        .content(objectMapper.writeValueAsString(faculty))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.id").value("1"))
                .andExpect((ResultMatcher) jsonPath("$.name").value("Медведи"))
                .andExpect((ResultMatcher) jsonPath("$.color").value("red"));

    }

    @Test
    void editFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Медведи", "red");
        when(facultyRepository.save(ArgumentMatchers.any(Faculty.class))).thenReturn(faculty);
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
//        mockMvc.perform(put("/faculty/1" + faculty.getId())
        mockMvc.perform(put("/faculty/1")
                        .content(objectMapper.writeValueAsString(faculty))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
//                .andExpect((ResultMatcher) jsonPath("$.id").value("1"))
//                .andExpect((ResultMatcher) jsonPath("$.name").value("Медведи"))
//                .andExpect((ResultMatcher) jsonPath("$.color").value("red"));
    }

    @Test
    void deleteFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Бобры", "white");
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
//        mockMvc.perform(put("/faculty/1" + faculty.getId())
        mockMvc.perform(delete("/faculty/1")
                        .content(objectMapper.writeValueAsString(faculty))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
//                .andExpect((ResultMatcher) jsonPath("$.id").value("1"))
//                .andExpect((ResultMatcher) jsonPath("$.name").value("Бобры"))
//                .andExpect((ResultMatcher) jsonPath("$.color").value("white"));
    }

    @Test
    void getAll() throws Exception {
        when(facultyRepository.findAll()).thenReturn(Arrays.asList(
                new Faculty(1l, "Бобры", "white"),
                new Faculty(2l, "Еноты", "green")
        ));
        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/all-faculities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$").isArray())
                .andExpect((ResultMatcher) jsonPath("$[0].id").value(1L))
                .andExpect((ResultMatcher) jsonPath("$[1].id").value(2L));
    }

    @Test
    void filteredByColor() throws Exception {
        when(facultyRepository.findAllByColor("black")).thenReturn(Arrays.asList(
                new Faculty(1l, "Бобры", "black"),
                new Faculty(2l, "Еноты", "green"),
                new Faculty(3l, "Орлы", "blue"),
                new Faculty(4l, "Медведи", "red")
        ));
        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/color?color=black")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect((ResultMatcher) jsonPath("$").isArray())
                .andExpect((ResultMatcher) jsonPath("$[0].id").value(1L));
    }

    @Test
    void filteredByColorOrName() throws Exception {
        when(facultyRepository.findAllByColorLikeIgnoreCaseOrNameLikeIgnoreCase("blue", "Орлы")).thenReturn(Arrays.asList(
                new Faculty(1l, "Бобры", "black"),
                new Faculty(2l, "Еноты", "green"),
                new Faculty(3l, "Орлы", "blue"),
                new Faculty(4l, "Медведи", "red")
        ));
        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/by-color-or-name?colorOrName=blue&colorOrName=Орлы")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect((ResultMatcher) jsonPath("$").isArray());
    }
}
