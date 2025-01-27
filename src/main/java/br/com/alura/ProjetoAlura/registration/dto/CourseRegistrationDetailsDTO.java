package br.com.alura.ProjetoAlura.registration;

public record CourseRegistrationDetailsDTO(
    String name,
    String code,
    String instructorName,
    String instructorEmail,
    Long registrationCount
) {}