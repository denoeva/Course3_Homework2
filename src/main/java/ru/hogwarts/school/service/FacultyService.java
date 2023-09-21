package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exceptions.FacultyNotFoundException;
import ru.hogwarts.school.exceptions.StudentNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.FacultyRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.*;

@Service
public class FacultyService {
    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;

    public FacultyService(FacultyRepository facultyRepository,
                          StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }

    public Faculty addFaculty(Faculty faculty) {
        logger.info("Method started - 'addFaculty'");
        return facultyRepository.save(faculty);
    }

    public Collection<Faculty> getAllFaculities() {
        logger.info("Method started - 'getAllFaculities'");
        return facultyRepository.findAll();
    }

    public Faculty findFaculty(long id) {
        logger.info("Method started - 'findFaculty'");
        return facultyRepository.findById(id).orElseThrow(FacultyNotFoundException::new);
    }

    public Faculty editFaculty(Faculty faculty) {
        logger.info("Method started - 'editFaculty'");
        Faculty existingFaculty = facultyRepository.findById(faculty.getId()).orElseThrow(FacultyNotFoundException::new);
        existingFaculty.setColor(faculty.getColor());
        existingFaculty.setName(faculty.getName());
        return facultyRepository.save(existingFaculty);
    }

    public Faculty deleteFaculty(long id) {
        logger.info("Method started - 'deleteFaculty'");
        Faculty facultyD = facultyRepository.findById(id).orElseThrow(FacultyNotFoundException::new);
        facultyRepository.delete(facultyD);
        return facultyD;
    }

    public Collection<Faculty> findAllFaculties() {
        logger.info("Method started - 'findAllFaculties'");
        return facultyRepository.findAll();
    }

    public Collection<Faculty> findByColor(String color) {
        logger.info("Method started - 'findByColor'");
        return facultyRepository.findAllByColor(color);
    }

    public Collection<Faculty> findAllByColorOrName(String color, String name) {
        logger.info("Method started - 'findAllByColorOrName'");
        return facultyRepository.findAllByColorLikeIgnoreCaseOrNameLikeIgnoreCase(color, name);
    }

    public Faculty findByStudentId(Long studentId) {
        logger.info("Method started - 'findByStudentId'");
        return studentRepository.findById(studentId)
                .map(Student::getFaculty)
                .orElseThrow(StudentNotFoundException::new);
    }

    public String getTheLongestFacultyName() {
        return facultyRepository.findAll()
                .stream()
                .map(Faculty::getName)
                .max(Comparator.comparingInt(String::length))
                .orElseThrow(FacultyNotFoundException::new);
    }
}
