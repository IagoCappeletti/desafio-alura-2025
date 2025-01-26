package br.com.alura.ProjetoAlura.course;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class CourseController {

    @Autowired
    private CourseRepository repository;

    /*Method for registering a new course and sending confirmation in the body of the REQ*/
    @Transactional
    @PostMapping("/course/new")
    public ResponseEntity createCourse(@Valid @RequestBody NewCourseDTO newCourse) {
        Course course = newCourse.toModel();
        repository.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }

    @PostMapping("/course/{code}/inactive")
    public ResponseEntity createCourse(@PathVariable("code") String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("O código do curso é obrigatório.");
        }

        try {
            Optional<Course> optionalCourse = repository.findByCode(courseCode);

            if (optionalCourse.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso não encontrado.");
            }

            Course course = optionalCourse.get();
            if (course.getStatus() == CourseStatus.INACTIVE) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("O curso já está inativo.");
            }

            course.inactivate();
            repository.save(course);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar a inativação do curso: " + e.getMessage());
        }
    }
}
