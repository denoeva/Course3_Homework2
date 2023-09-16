package ru.hogwarts.school.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
    private final AvatarService avatarService;

    public StudentController(StudentService studentService,
                             AvatarService avatarService) {
        this.studentService = studentService;
        this.avatarService = avatarService;
    }

    @GetMapping("{id}")
    public ResponseEntity<Student> getStudentInfo(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.addStudent(student);
    }

    @PutMapping("{id}")
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        Student foundStudent = studentService.editStudent(student);
        if (foundStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> getAllStudent() {
        return ResponseEntity.ok(studentService.getAll());
    }

    @GetMapping("/age")
    public Collection<Student> filtered(@RequestParam int age) {
        return studentService.findByAge(age);
    }

    @GetMapping("/age-between")
    public Collection<Student> ageBetween(@RequestParam int min, @RequestParam int max) {
        return studentService.findByAgeBetween(min, max);
    }

    @GetMapping("/by-faculty")
    public Collection<Student> findBySFacultyId (Long facultyId) {
        return studentService.findByFacultyId(facultyId);
    }

    @PostMapping(value = "/{studentId}/avatar",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> save(@PathVariable Long studentId, @RequestBody MultipartFile multipartFile){
        try {
            return ResponseEntity.ok(avatarService.save(studentId,multipartFile));
        } catch (IOException e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/reports/count")
    public Long getCountStudent() {
        return studentService.getCountOfStudent();
    }
    @GetMapping("/reports/average-age")
    public Double getAverageAgeStudents() {
        return studentService.getAverageAgeStudents();
    }
    @GetMapping("/reports/last-five")
    public Collection<Student> findLastFiveStudents() {
        return  studentService.lastFiveStudent();
    }
}
