package br.com.alura.ProjetoAlura.course.repository;

import br.com.alura.ProjetoAlura.course.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
    Optional<CourseEntity> findByCode(String courseCode);

    boolean existsByCode(String courseCode);
}
