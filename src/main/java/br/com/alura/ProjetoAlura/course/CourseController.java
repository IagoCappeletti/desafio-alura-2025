package br.com.alura.ProjetoAlura.course;

import br.com.alura.ProjetoAlura.course.dto.NewCourseDTO;
import br.com.alura.ProjetoAlura.course.entity.CourseEntity;
import br.com.alura.ProjetoAlura.course.entity.CourseStatus;
import br.com.alura.ProjetoAlura.course.repository.CourseRepository;
import br.com.alura.ProjetoAlura.user.entity.Role;
import br.com.alura.ProjetoAlura.user.entity.User;
import br.com.alura.ProjetoAlura.user.repository.UserRepository;
import br.com.alura.ProjetoAlura.util.ErrorItemDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@RestController
public class CourseController {

    private CourseRepository repository;
    private UserRepository userRepository;

    CourseController(CourseRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Transactional
    @PostMapping("/course/new")
    public ResponseEntity createCourse(@Valid @RequestBody NewCourseDTO newCourse, UriComponentsBuilder uriBuilder) {
        if(repository.existsByCode(newCourse.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("code", "This course is already registered"));
        }

        String instructorEmail = newCourse.getInstructorEmail();
        Optional<User> userOptional = userRepository.findByEmail(instructorEmail);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorItemDTO("instructorEmail", "Supervisor not found"));
        }

        User user = userOptional.get();

        if(user.getRole() != Role.INSTRUCTOR){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("code", "The supervisor must be an instructor"));
        }

        CourseEntity course = newCourse.toModel();
        repository.save(course);

        var uri = uriBuilder.path("/course/{code}").buildAndExpand(course.getCode()).toUri();
        return ResponseEntity.created(uri).body(course);
    }

    /*I believe that in this endpoint, the most appropriate method would be a PUT to update an existing resource in an
    idempotent manner. The POST method, which was already included in the project, is generally used to create new resources.*/
    @PutMapping("/course/{code}/inactive")
    public ResponseEntity inactivateCourse(@PathVariable("code") String courseCode) {
        try {
            if (courseCode == null || courseCode.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ErrorItemDTO("code", "Course code is required.")
                );
            }

            Optional<CourseEntity> optionalCourse = repository.findByCode(courseCode);

            if (optionalCourse.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ErrorItemDTO("code", "Course code does not exist.")
                );
            }

            CourseEntity course = optionalCourse.get();

            if (course.getStatus() == CourseStatus.INACTIVE) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new ErrorItemDTO("code", "The course is already inactive."));
            }

            course.inactivate();
            CourseEntity inactivatedCourse = repository.save(course);

            return ResponseEntity.status(HttpStatus.OK).body(inactivatedCourse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ErrorItemDTO("code", "Error processing course inactivation." + e.getMessage()));
        }
    }
}
