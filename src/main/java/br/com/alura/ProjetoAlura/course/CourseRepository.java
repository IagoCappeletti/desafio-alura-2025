package br.com.alura.ProjetoAlura.course;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String courseCode);

    boolean existsByCode(String courseCode);
}
