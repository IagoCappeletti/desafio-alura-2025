package br.com.alura.ProjetoAlura.course;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String name;
    private String instructorEmail;
    private String description;

    @Enumerated(EnumType.STRING)
    private CourseStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    /*Used to control information related to dates and times*/
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    private LocalDateTime deactivationDate;


    public Course() {}

    public Course(String code, String name, String instructorEmail, String description, CourseStatus status) {
        this.code = code;
        this.name = name;
        this.instructorEmail = instructorEmail;
        this.description = description;
        this.status = status;
    }


    public String getCode() {return code;}

    public String getName() {return name;}

    public String getInstructorEmail() {return instructorEmail;}

    public String getDescription() {return description;}

    public CourseStatus getStatus() {return status;}

    public LocalDateTime getLocalDateTime() {return createdAt;}

    public void inactivate() {
        this.status = CourseStatus.INACTIVE;
        this.deactivationDate = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

}
