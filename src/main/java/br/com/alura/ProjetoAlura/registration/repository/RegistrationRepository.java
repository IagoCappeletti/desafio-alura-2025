package br.com.alura.ProjetoAlura.registration.repository;

import br.com.alura.ProjetoAlura.course.entity.CourseEntity;
import br.com.alura.ProjetoAlura.registration.dto.CourseRegistrationDetailsDTO;
import br.com.alura.ProjetoAlura.registration.entity.CourseRegistration;
import br.com.alura.ProjetoAlura.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<CourseRegistration, Long> {

    boolean existsByUserAndCourse(User user, CourseEntity course);

    @Query("""
    SELECT new br.com.alura.ProjetoAlura.registration.CourseRegistrationDetailsDTO(
        c.name,
        c.code,
        c.instructorName,
        c.instructorEmail,
        COUNT(r.course.id)
    )
    FROM CourseEntity c
    LEFT JOIN CourseRegistration r ON c.id = r.course.id
    WHERE c.status = 'ACTIVE'
    GROUP BY c.name, c.code, c.instructorName, c.instructorEmail
""")
    List<CourseRegistrationDetailsDTO> findCourseDetailsWithRegistrationCount();
}
