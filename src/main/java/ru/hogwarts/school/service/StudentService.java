package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exceptions.FacultyNotFoundException;
import ru.hogwarts.school.exceptions.StudentNotFoundException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class StudentService {
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    public StudentService(StudentRepository studentRepository,
                          FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    public Student addStudent(Student student) {
        logger.info("Method started - 'addStudent'");
        return studentRepository.save(student);
    }

    public Student findStudent(long id) {
        logger.info("Method started - 'findStudent'");
        return studentRepository.findById(id).orElseThrow(StudentNotFoundException::new);
    }

    public Student editStudent(Student student) {
        logger.info("Method started - 'editStudent'");
        Student existingStudent = studentRepository.findById(student.getId()).orElseThrow(StudentNotFoundException::new);
        existingStudent.setAge(student.getAge());
        existingStudent.setName(student.getName());
        return studentRepository.save(existingStudent);
    }

    public Student deleteStudent(long id) {
        logger.info("Method started - 'deleteStudent'");
        Student studentD = studentRepository.findById(id).orElseThrow(StudentNotFoundException::new);
        studentRepository.delete(studentD);
        return studentD;
    }

    public Collection<Student> getAll() {
        logger.info("Method started - 'getAll'");
        return studentRepository.findAll();
    }

    public Collection<Student> findByAge(int age) {
        logger.info("Method started - 'findByAge'");
        return studentRepository.findAllByAge(age);
    }

    public Collection<Student> findByAgeBetween(int min, int max) {
        logger.info("Method started - 'findByAgeBetween'");
        return studentRepository.findAllByAgeBetween(min, max);
    }
    
    public Collection<Student> findByFacultyId (Long facultyId) {
        logger.info("Method started - 'findByFacultyId'");
        return facultyRepository.findById(facultyId)
                .map(Faculty::getStudents)
                .orElseThrow(FacultyNotFoundException::new);
    }

    public Long getCountOfStudent() {
        logger.info("Method started - 'getCountOfStudent'");
        return studentRepository.getCountOfStudents();
    }

    public Double getAverageAgeStudents() {
        logger.info("Method started - 'getAverageAgeStudents'");
        return studentRepository.getAverageAgeOfStudents();
    }

    public Collection<Student> lastFiveStudent() {
        logger.info("Method started - 'lastFiveStudent'");
        return studentRepository.findLastFiveStudents();
    }

    public List<String> getNamesStartedFrom(char firstSymbol) {
        return studentRepository.findAll()
                .stream()
                .map(Student::getName)
                .filter(student -> Character.toLowerCase(student.charAt(0))
                        == Character.toLowerCase(firstSymbol))
                .collect(Collectors.toList());
    }

    public double getAverageAge() {
        return studentRepository.findAll()
                .stream()
                .mapToInt(Student::getAge)
                .average()
                .orElseThrow(StudentNotFoundException::new);
    }

    public void printStudentsListAsync() {
        List<Student> all = studentRepository.findAll();
        System.out.println(all.get(0).getName());
        System.out.println(all.get(1).getName());

        new Thread(() -> {
            System.out.println(all.get(2).getName());
            System.out.println(all.get(3).getName());
        }).start();

        new Thread(() -> {
            System.out.println(all.get(4).getName());
            System.out.println(all.get(5).getName());
        }).start();
    }

    public void printStudentsListSync() {
        List<Student> all = studentRepository.findAll();
        printStudentsListSync(all.get(0).getName());
        printStudentsListSync(all.get(1).getName());

        new Thread(() -> {
            printStudentsListSync(all.get(2).getName());
            printStudentsListSync(all.get(3).getName());
        }).start();

        new Thread(() -> {
            printStudentsListSync(all.get(4).getName());
            printStudentsListSync(all.get(5).getName());
        }).start();
    }

    private synchronized void printStudentsListSync(String name) {
        System.out.println(name);
    }
}
