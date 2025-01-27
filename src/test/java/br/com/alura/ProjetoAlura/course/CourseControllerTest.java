package br.com.alura.ProjetoAlura.course;

import br.com.alura.ProjetoAlura.course.dto.NewCourseDTO;
import br.com.alura.ProjetoAlura.course.entity.CourseEntity;
import br.com.alura.ProjetoAlura.course.entity.CourseStatus;
import br.com.alura.ProjetoAlura.course.repository.CourseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
public class CourseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void newCourse__should_return_bad_request_when_name_is_blank() throws Exception {
        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setName("");
        newCourseDTO.setCode("ABCDE");
        newCourseDTO.setDescription("Description");
        newCourseDTO.setInstructorEmail("Iago@gmail.com");
        newCourseDTO.setInstructorName("Iago");

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("name"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newCode__should_return_bad_request_when_name_is_blank() throws Exception {
        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setName("java");
        newCourseDTO.setCode("");
        newCourseDTO.setDescription("Description");
        newCourseDTO.setInstructorEmail("Iago@gmail.com");
        newCourseDTO.setInstructorName("Iago");

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("code"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newInstructorName__should_return_bad_request_when_name_is_blank() throws Exception {
        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setName("Iago");
        newCourseDTO.setCode("ABCDE");
        newCourseDTO.setDescription("Description");
        newCourseDTO.setInstructorEmail("Iago@gmail.com");
        newCourseDTO.setInstructorName("");

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("instructorName"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newInstructorEmail__should_return_bad_request_when_name_is_blank() throws Exception {
        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setName("Iago");
        newCourseDTO.setCode("ABCDE");
        newCourseDTO.setDescription("Description");
        newCourseDTO.setInstructorEmail("");
        newCourseDTO.setInstructorName("Iago");

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("instructorEmail"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void newCourse__should_return_bad_request_when_code_already_exists() throws Exception {
        NewCourseDTO newCourseDTO = new NewCourseDTO();
        newCourseDTO.setName("Iago");
        newCourseDTO.setCode("ABCDE");
        newCourseDTO.setDescription("Description");
        newCourseDTO.setInstructorEmail("iago@gmail.com");
        newCourseDTO.setInstructorName("Iago");

        when(courseRepository.existsByCode(newCourseDTO.getCode())).thenReturn(true);

        mockMvc.perform(post("/course/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.field").value("code"))
                .andExpect(jsonPath("$.message").value("This course is already registered"));
    }

    @Test
    void createCourse__should_return_conflict_when_course_is_already_inactive() throws Exception {
        String courseCode = "java-one";
        CourseEntity course = new CourseEntity(courseCode, CourseStatus.INACTIVE);

        when(courseRepository.findByCode(courseCode)).thenReturn(Optional.of(course));

        mockMvc.perform(post("/course/java-one/inactive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.field").value("code"))
                .andExpect(jsonPath("$.message").value("The course is already inactive."));
    }

    @Test
    void createCourse__should_return_not_found_when_course_code_doesnt_exist() throws Exception {
        String courseCode = "java-one";

        when(courseRepository.findByCode(courseCode)).thenReturn(Optional.empty());

        mockMvc.perform(post("/course/java-one/inactive")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.field").value("code"))
                .andExpect(jsonPath("$.message").value("Course code does not exist."));
    }
}
