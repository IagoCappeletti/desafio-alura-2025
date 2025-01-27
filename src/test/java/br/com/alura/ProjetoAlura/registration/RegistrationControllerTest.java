package br.com.alura.ProjetoAlura.registration;


import br.com.alura.ProjetoAlura.course.entity.CourseEntity;
import br.com.alura.ProjetoAlura.course.repository.CourseRepository;
import br.com.alura.ProjetoAlura.course.entity.CourseStatus;
import br.com.alura.ProjetoAlura.registration.dto.CourseRegistrationDetailsDTO;
import br.com.alura.ProjetoAlura.registration.dto.NewRegistrationDTO;
import br.com.alura.ProjetoAlura.registration.entity.CourseRegistration;
import br.com.alura.ProjetoAlura.registration.repository.RegistrationRepository;
import br.com.alura.ProjetoAlura.user.entity.Role;
import br.com.alura.ProjetoAlura.user.entity.User;
import br.com.alura.ProjetoAlura.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;


import java.util.List;
import java.util.Optional;

@WebMvcTest(RegistrationController.class)
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RegistrationRepository registrationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createRegistration__should_return_not_found_when_course_is_inactive_or_not_found() throws Exception {
        NewRegistrationDTO newRegistrationDTO = new NewRegistrationDTO();
        newRegistrationDTO.setCourseCode("java-one");
        newRegistrationDTO.setStudentEmail("student@example.com");

        when(courseRepository.findByCode(newRegistrationDTO.getCourseCode())).thenReturn(Optional.empty());

        mockMvc.perform(post("/registration/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRegistrationDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.field").value("Code"))
                .andExpect(jsonPath("$.message").value("Course does not exist or is inactive! Please try again with another code."));
    }

    @Test
    void createRegistration__should_return_not_found_when_user_does_not_exist() throws Exception {

        NewRegistrationDTO newRegistrationDTO = new NewRegistrationDTO();
        newRegistrationDTO.setCourseCode("java-one");
        newRegistrationDTO.setStudentEmail("student@example.com");

        CourseEntity course = new CourseEntity(
                "java-one",
                "Java Programming 101",
                "John Doe",
                "john.doe@example.com",
                "An introductory course to Java programming.",
                CourseStatus.ACTIVE

        );

        when(courseRepository.findByCode(newRegistrationDTO.getCourseCode())).thenReturn(Optional.of(course));

        when(userRepository.findByEmail(newRegistrationDTO.getStudentEmail())).thenReturn(Optional.empty());

        mockMvc.perform(post("/registration/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRegistrationDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.field").value("Email"))
                .andExpect(jsonPath("$.message").value("Non-existent user!"));
    }

    @Test
    void createRegistration__should_return_created_when_registration_is_successful() throws Exception {

        NewRegistrationDTO newRegistrationDTO = new NewRegistrationDTO();
        newRegistrationDTO.setCourseCode("java-one");
        newRegistrationDTO.setStudentEmail("student@example.com");

        CourseEntity course = new CourseEntity(
                "java-one",
                "Java Programming 101",
                "John Doe",
                "john.doe@example.com",
                "An introductory course to Java programming.",
                CourseStatus.ACTIVE

        );

        User user = new User("User 1", "user1@test.com", Role.STUDENT,"mudar123");


        CourseRegistration registration = new CourseRegistration();
        registration.setCourse(course);
        registration.setUser(user);


        when(courseRepository.findByCode(newRegistrationDTO.getCourseCode())).thenReturn(Optional.of(course));


        when(userRepository.findByEmail(newRegistrationDTO.getStudentEmail())).thenReturn(Optional.of(user));


        when(registrationRepository.existsByUserAndCourse(user, course)).thenReturn(false);


        when(registrationRepository.save(any(CourseRegistration.class))).thenReturn(registration);

        mockMvc.perform(post("/registration/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRegistrationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.course.code").value(course.getCode()))
                .andExpect(jsonPath("$.user.email").value(user.getEmail()));
    }

    @Test
    void report_should_return_ok_with_registration_report() throws Exception {

        CourseRegistrationDetailsDTO course1 = new CourseRegistrationDetailsDTO(
                "Java Programming",
                "java-101",
                "iago Doe",
                "Iagodoe@example.com",
                50L
        );

        CourseRegistrationDetailsDTO course2 = new CourseRegistrationDetailsDTO(
                "Python Basics",
                "python-101",
                "Jailson Smith",
                "Jailson.smith@example.com",
                30L
        );

        List<CourseRegistrationDetailsDTO> courseDetails = List.of(course1, course2);

        when(registrationRepository.findCourseDetailsWithRegistrationCount()).thenReturn(courseDetails);

        mockMvc.perform(get("/registration/report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseName").value(course1.name()))
                .andExpect(jsonPath("$[0].courseCode").value(course1.code()))
                .andExpect(jsonPath("$[0].instructorName").value(course1.instructorName()))
                .andExpect(jsonPath("$[0].instructorEmail").value(course1.instructorEmail()))
                .andExpect(jsonPath("$[0].totalRegistrations").value(course1.registrationCount()))
                .andExpect(jsonPath("$[1].courseName").value(course2.name()))
                .andExpect(jsonPath("$[1].courseCode").value(course2.code()))
                .andExpect(jsonPath("$[1].instructorName").value(course2.instructorName()))
                .andExpect(jsonPath("$[1].instructorEmail").value(course2.instructorEmail()))
                .andExpect(jsonPath("$[1].totalRegistrations").value(course2.registrationCount()));
    }


}
