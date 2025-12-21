package domain;

import java.time.LocalDate;


public class Student {
    // ======= Atributos =======
    private Long id;
    private String name;
    private final String cpf;
    private String email;
    private LocalDate birthDate;

    // ======= Construtores =======

    public Student(Long id, String name, String cpf, String email, LocalDate birthDate) {
        this.id = id;
        setName(name);
        this.cpf = validateCpf(cpf);
        setEmail(email);
        this.birthDate = birthDate;
    }

    public static Student newStudent(String name, String cpf, String email, LocalDate birthDate) {
        return new Student(null, name, cpf, email, birthDate);
    }


    // ======= Validações internas =======

    private static String notBlank(String s, String msg) {
        if (s == null || s.trim().isEmpty()) {
            throw new IllegalArgumentException(msg);
        }
        return s.trim();
    }

    private static String cleanDigits(String s) {
        return s.replaceAll("\\D", "");
    }


    // ======= Setters com validação =======

    public void setName(String name) {
        String n = notBlank(name, "Nome obrigatório");
        if (n.length() > 120) {
            throw new IllegalArgumentException("Nome inválido (máx 120 caracteres)");
        }
        this.name = n;
    }

    private static String validateCpf(String cpf) {
        String d = cleanDigits(notBlank(cpf, "Cpf obrigatório"));
        if (d.length() != 11) {
            throw new IllegalArgumentException("Cpf deve ter 11 dígitos");
        }
        return d;
    }

    public void setEmail(String email) {
        String e = notBlank(email, "Email obrigatório");
        if (!e.contains("@") || !e.contains(".")) {
            throw new IllegalArgumentException("Email inválido");
        }
        this.email = e;
    }

    //getters e setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public String getCpf() {
        return cpf;
    }
    public String getEmail() {
        return email;
    }
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    public LocalDate getBirthDate() {
        return birthDate;
    }

    @Override
    public String toString() {
        return "Student{id = %s, name = '%s', cpf = '%s', email = '%s', birthDate = %s}"
                .formatted(id, name, cpf, email, birthDate);
    }
}