package repository;

import domain.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    void save(Student s);
    void update(Student s);
    void deleteById(Long id);

    Optional<Student> findById(Long id);
    Optional<Student> findByCpf(String cpfDigits);
    List<Student> list(int page, int size);
}
