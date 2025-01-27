package br.com.alura.ProjetoAlura.exceptions;

public class CourseAlreadyExistsException extends RuntimeException{
    public CourseAlreadyExistsException(String message) {
        super(message);
    }
}
