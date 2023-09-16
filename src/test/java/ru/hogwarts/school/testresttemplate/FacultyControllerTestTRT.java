package ru.hogwarts.school.testresttemplate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.Course3Homework2HogwartsSchoolApplication;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Course3Homework2HogwartsSchoolApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTestTRT {

    public static final Faculty BEAVERS = new Faculty(null, "Бобры", "white");
    public static final Faculty RACCOONS = new Faculty(null, "Еноты", "зеленый");
    @Autowired
    TestRestTemplate template;
    @Autowired
    FacultyRepository facultyRepository;
    @Autowired
    StudentRepository studentRepository;

    @BeforeEach
    void init() {
        template.postForEntity("/faculty", BEAVERS, Faculty.class);
        template.postForEntity("/faculty", RACCOONS, Faculty.class);
    }

    @Autowired
    void clearDB() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    private ResponseEntity<Faculty> createFaculty(String name, String color) {
        Faculty request = new Faculty();
        request.setName(name);
        request.setColor(color);
        ResponseEntity<Faculty> response = template.postForEntity("/faculty", request, Faculty.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response;
    }

    @Test
    void create() {
        ResponseEntity<Faculty> response = createFaculty("Бобры", "white");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Бобры");
        assertThat(response.getBody().getColor()).isEqualTo("white");
    }

    @Test
    void getById() {
        ResponseEntity<Faculty> faculty = createFaculty("Бобры", "white");
        assertThat(faculty.getBody()).isNotNull();
        Long id = faculty.getBody().getId();
        ResponseEntity<Faculty> response = template.getForEntity("/faculty/" + id, Faculty.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(id);
        assertThat(response.getBody().getName()).isEqualTo("Бобры");
        assertThat(response.getBody().getColor()).isEqualTo("white");
    }

    @Test
    void getAll() {
        ResponseEntity<Collection> response = template.getForEntity("/faculty/all-faculities", Collection.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        Collection<Faculty> body = response.getBody();
        assertThat(body.isEmpty()).isFalse();
        assertThat(body.size()).isEqualTo(2);
    }

    @Test
    void update() {
        ResponseEntity<Faculty> response = createFaculty("Бобры", "white");
        Faculty faculty = response.getBody();
        faculty.setColor("white");
        template.put("/faculty/" + faculty.getId(), faculty);
        response = template.getForEntity("/faculty/" + faculty.getId(), Faculty.class);
        assertThat(response.getBody().getColor()).isEqualTo("white");
    }

    @Test
    void delete() {
        ResponseEntity<Faculty> response = createFaculty("Бобры", "white");
        template.delete("/faculty/id" + response.getBody().getId());
        response = template.getForEntity("/id" + response.getBody().getId(), Faculty.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void filtered() {
        ResponseEntity<Collection> response = template
                .getForEntity("/faculty/color?color=white", Collection.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(1);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void colorByName() {
        ResponseEntity<Collection> response = template
                .getForEntity("/faculty/by-color-or-name?colorOrName=white", Collection.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(1);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void byStudent() {
        ResponseEntity<Faculty> response = createFaculty("Бобры", "white");
        Faculty expectedFaculty = response.getBody();
        Student student = new Student();
        student.setFaculty(expectedFaculty);
        ResponseEntity<Student> studentResponseEntity = template.postForEntity("/student", student, Student.class);
        Long studentId = studentResponseEntity.getBody().getId();
        response = template.getForEntity("/faculty/by-student?studentdId=" + studentId, Faculty.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(expectedFaculty);
    }
}
