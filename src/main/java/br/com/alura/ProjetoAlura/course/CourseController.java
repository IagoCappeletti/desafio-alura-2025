package br.com.alura.ProjetoAlura.courseEntity;

import br.com.alura.ProjetoAlura.util.ErrorItemDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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

    private CourseRepository repository;

    CourseController(CourseRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @PostMapping("/course/new")
    public ResponseEntity createCourse(@Valid @RequestBody NewCourseDTO newCourse, UriComponentsBuilder uriBuilder) {
        if(repository.existsByCode(newCourse.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("code", "This course is already registered"));
        }

        Course course = newCourse.toModel();
        repository.save(course);

        var uri = uriBuilder.path("/course/{code}").buildAndExpand(course.getCode()).toUri();
        return ResponseEntity.created(uri).body(course);
    }

    @PostMapping("/course/{code}/inactive")
    public ResponseEntity createCourse(@PathVariable("code") String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Course code is required.");
        }

        try {
            Optional<Course> optionalCourse = repository.findByCode(courseCode);

            if (optionalCourse.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ErrorItemDTO("code", "Course code does not exist.")
                );
            }

            Course course = optionalCourse.get();
            if (course.getStatus() == CourseStatus.INACTIVE) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new ErrorItemDTO("code", "The course is already inactive."));
            }

            course.inactivate();
            repository.save(course);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ErrorItemDTO("code", "Error processing course inactivation." + e.getMessage()));
        }
    }
}
