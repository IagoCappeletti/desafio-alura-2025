package br.com.alura.ProjetoAlura.registration;

import br.com.alura.ProjetoAlura.course.entity.CourseEntity;
import br.com.alura.ProjetoAlura.course.repository.CourseRepository;
import br.com.alura.ProjetoAlura.course.entity.CourseStatus;
import br.com.alura.ProjetoAlura.registration.dto.NewRegistrationDTO;
import br.com.alura.ProjetoAlura.registration.entity.CourseRegistration;
import br.com.alura.ProjetoAlura.registration.entity.RegistrationReportItem;
import br.com.alura.ProjetoAlura.registration.repository.RegistrationRepository;
import br.com.alura.ProjetoAlura.user.entity.User;
import br.com.alura.ProjetoAlura.user.repository.UserRepository;
import br.com.alura.ProjetoAlura.util.ErrorItemDTO;
import jakarta.validation.Valid;
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

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;

    RegistrationController(CourseRepository courseRepository, UserRepository userRepository, RegistrationRepository registrationRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
    }

    @PostMapping("/registration/new")
    public ResponseEntity createCourse(@Valid @RequestBody NewRegistrationDTO newRegistration, UriComponentsBuilder uriBuilder) {
        Optional<CourseEntity> course = courseRepository.findByCode((newRegistration.getCourseCode()));
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
