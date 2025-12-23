package service;

import domain.Student;
import repository.StudentRepository;

import java.util.List;

public class StudentService {

    private final StudentRepository repo;

    public StudentService (StudentRepository repo) {
        this.repo = repo;
    }

    public Student create(Student s) {
        repo.findByCpf(s.getCpf()).ifPresent(x -> {
            throw new IllegalArgumentException("Já existe aluno com este CPF");
        });
        repo.save(s);
        return s;
    }

    public Student update(Long id, String name, String email) {
        Student s = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
        if (name != null && !name.isBlank()) s.setName(s.getName());
        if (email != null && !email.isBlank()) s.setEmail(s.getEmail());
        repo.update(s);
        return s;
    }

    public void delete (Long id) {
        repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
        repo.deleteById(id);
    }

    public Student find (Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
    }

    public List<Student> list (int page, int size) {
        int p = Math.max(0, page);
        int s = Math.min(Math.max(1, size), 100);
        return repo.list(p, s);
    }
}
