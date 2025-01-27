package br.com.alura.ProjetoAlura.registration;

import br.com.alura.ProjetoAlura.course.Course;
import br.com.alura.ProjetoAlura.course.CourseRepository;
import br.com.alura.ProjetoAlura.course.CourseStatus;
import br.com.alura.ProjetoAlura.user.User;
import br.com.alura.ProjetoAlura.user.UserRepository;
import br.com.alura.ProjetoAlura.util.ErrorItemDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class RegistrationController {

    @Autowired
    CourseRepository courseRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RegistrationRepository registrationRepository;

    @PostMapping("/registration/new")
    public ResponseEntity createCourse(@Valid @RequestBody NewRegistrationDTO newRegistration, UriComponentsBuilder uriBuilder) {
        Optional<Course> course = courseRepository.findByCode((newRegistration.getCourseCode()));
        if (course.isEmpty() || course.get().getStatus() != CourseStatus.ACTIVE) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorItemDTO("Code", "Course does not exist or is inactive! Please try again with another code."));
        }

        Optional<User> user = userRepository.findByEmail(newRegistration.getStudentEmail());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorItemDTO("Email", "Non-existent user!"));
        }

        if (registrationRepository.existsByUserAndCourse(user.get(), course.get())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("Code", "User is already registered in this course"));
        }

        CourseRegistration registration = new CourseRegistration();
        registration.setCourse(course.get());
        registration.setUser(user.get());
        registrationRepository.save(registration);

        var uri = uriBuilder.path("/registration/{id}").buildAndExpand(registration.getId()).toUri();
        return ResponseEntity.created(uri).body(registration);


    }

    @GetMapping("/registration/report")
    public ResponseEntity<List<RegistrationReportItem>> report() {

        var courseRanked =  registrationRepository.findCourseDetailsWithRegistrationCount();
        List<RegistrationReportItem> items = courseRanked.stream()
                .map(courseDetail -> new RegistrationReportItem(
                        courseDetail.name(),
                        courseDetail.code(),
                        courseDetail.instructorName(),
                        courseDetail.instructorEmail(),
                        courseDetail.registrationCount()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(items);
    }

}
