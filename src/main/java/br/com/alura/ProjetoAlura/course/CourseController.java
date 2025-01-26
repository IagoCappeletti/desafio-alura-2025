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
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@RestController
public class CourseController {

    @Autowired
    private CourseRepository repository;

    /*Method for registering a new course and sending confirmation in the body of the REQ*/
    @Transactional
    @PostMapping("/course/new")
    public ResponseEntity createCourse(@Valid @RequestBody NewCourseDTO newCourse, UriComponentsBuilder uriBuilder) {
        Course course = newCourse.toModel();
        repository.save(course);

        var uri = uriBuilder.path("/course/{code}").buildAndExpand(course.getCode()).toUri();
        return ResponseEntity.created(uri).body(course);
    }

    @PostMapping("/course/{code}/inactive")
    public ResponseEntity createCourse(@PathVariable("code") String courseCode) {
        // TODO: Implementar a Questão 2 - Inativação de Curso aqui...

        return ResponseEntity.ok().build();
    }

}
