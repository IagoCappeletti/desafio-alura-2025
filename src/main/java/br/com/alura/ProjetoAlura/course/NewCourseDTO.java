package br.com.alura.ProjetoAlura.course;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static br.com.alura.ProjetoAlura.course.CourseStatus.ACTIVE;

public class NewCourseDTO {

    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z-]{4,10}$", message = "The code must be between 4 and 10 characters long, without spaces or numbers, and may include hyphens.")
    private String code;

    private String description;
    @NotBlank
    private String instructorName;

    @NotBlank
    @Email(message = "Instructor email is required.")
    private String instructorEmail;

    public NewCourseDTO() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructorName() {return instructorName;}

    public String getInstructorEmail() {
        return instructorEmail;
    }

    public void setInstructorEmail(String instructorEmail) {
        this.instructorEmail = instructorEmail;
    }

    public Course toModel() {
        return new Course(code,name, instructorName, instructorEmail, description, ACTIVE);
    }
}
