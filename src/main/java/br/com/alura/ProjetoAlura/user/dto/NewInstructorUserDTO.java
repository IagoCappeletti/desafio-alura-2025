package br.com.alura.ProjetoAlura.user.dto;

import static br.com.alura.ProjetoAlura.user.entity.Role.INSTRUCTOR;
import br.com.alura.ProjetoAlura.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public class NewInstructorUserDTO extends User {

    @NotNull
    @Length(min = 3, max = 50)
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotNull
    @Length(min = 8, max = 16)
    private String password;

    public NewInstructorUserDTO() {}

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User toModel () {
        return new User(name, email, INSTRUCTOR, password);
    }

}
