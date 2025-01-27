package br.com.alura.ProjetoAlura.course.service;

import br.com.alura.ProjetoAlura.course.dto.NewCourseDTO;
import br.com.alura.ProjetoAlura.course.entity.CourseEntity;
import br.com.alura.ProjetoAlura.course.repository.CourseRepository;
import br.com.alura.ProjetoAlura.user.entity.Role;
import br.com.alura.ProjetoAlura.user.entity.User;
import br.com.alura.ProjetoAlura.user.repository.UserRepository;
import br.com.alura.ProjetoAlura.exceptions.CourseAlreadyExistsException;
import br.com.alura.ProjetoAlura.exceptions.InstructorNotFoundException;
import br.com.alura.ProjetoAlura.exceptions.InvalidInstructorRoleException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseService(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public CourseEntity createCourse(NewCourseDTO newCourse) {
        validateCourseCode(newCourse.getCode());
        validateInstructor(newCourse.getInstructorEmail());

        CourseEntity course = newCourse.toModel();
        courseRepository.save(course);

        return course;
    }

    public CourseEntity inactivateCourse(String courseCode) {
        CourseEntity course = courseRepository.findByCode(courseCode)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with code: " + courseCode));
        course.inactivate();
        return courseRepository.save(course);
    }

    private void validateCourseCode(String code) {
        if (courseRepository.existsByCode(code)) {
            throw new CourseAlreadyExistsException("This course is already registered: " + code);
        }
    }

    private User validateInstructor(String instructorEmail) {
        Optional<User> userOptional = userRepository.findByEmail(instructorEmail);

        if (userOptional.isEmpty()) {
            throw new InstructorNotFoundException("Instructor not found with email: " + instructorEmail);
        }

        User user = userOptional.get();
        if (user.getRole() != Role.INSTRUCTOR) {
            throw new InvalidInstructorRoleException("The user must have the role of INSTRUCTOR");
        }

        return user;
    }
}
