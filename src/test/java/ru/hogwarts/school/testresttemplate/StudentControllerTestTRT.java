package ru.hogwarts.school.testresttemplate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.Course3Homework2HogwartsSchoolApplication;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest(classes = Course3Homework2HogwartsSchoolApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTestTRT {
    public static final Student ELENA = new Student(null, "Елена", 31);
    public static final Student MARIA = new Student(null, "Мария", 37);
    @Autowired
    TestRestTemplate template;
    @Autowired
    FacultyRepository facultyRepository;
    @Autowired
    StudentRepository studentRepository;

    @BeforeEach
    void init() {
        template.postForEntity("/student", ELENA, Student.class);
        template.postForEntity("/student", MARIA, Student.class);
    }

    @Autowired
    void clearDB() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    private ResponseEntity<Student> createStudent(String name, int age) {
        Student request = new Student();
        request.setName(name);
        request.setAge(age);
        ResponseEntity<Student> response = template.postForEntity("/student", request, Student.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        return response;
    }

    @Test
    void create() {
        ResponseEntity<Student> response = createStudent("Варвара", 21);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Варвара");
        assertThat(response.getBody().getAge()).isEqualTo(21);
    }

    @Test
    void getById() {
        ResponseEntity<Student> student = createStudent("Варвара", 21);
        Long id = student.getBody().getId();
        ResponseEntity<Student> response = template.getForEntity("/student/" + id, Student.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(id);
        assertThat(response.getBody().getName()).isEqualTo("Варвара");
        assertThat(response.getBody().getAge()).isEqualTo(21);
    }

    @Test
    void getAll() {
        ResponseEntity<Collection> response = template.getForEntity("/student", Collection.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        Collection<Student> body = response.getBody();
        assertThat(body.isEmpty()).isFalse();
        assertThat(body.size()).isEqualTo(2);
    }

    @Test
    void update() {
        ResponseEntity<Student> response = createStudent("Варвара", 21);
        Student student = response.getBody();
        student.setAge(25);
        template.put("/student/" + student.getId(), student);
        response = template.getForEntity("/student/" + student.getId(), Student.class);
        assertThat(response.getBody().getAge()).isEqualTo(25);
    }

    @Test
    void delete() {
        ResponseEntity<Student> response = createStudent("Варвара", 21);
        template.delete("/student/" + response.getBody().getId());
        response = template.getForEntity("/student/" + response.getBody().getId(), Student.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void filtered() {
        ResponseEntity<Collection> response = template
                .getForEntity("/student/age?age=31", Collection.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(1);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void ageBetween() {
        ResponseEntity<Collection> response = template
                .getForEntity("/student/age-between?min=21&max=30", Collection.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(0);
        assertThat(response.getBody()).isNotNull();
    }
}
