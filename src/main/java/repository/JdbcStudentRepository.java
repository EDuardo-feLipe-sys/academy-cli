package repository;

import config.Database;
import domain.Student;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcStudentRepository implements StudentRepository {
    private final Database db;

    public JdbcStudentRepository(Database db) {
        this.db = db;
    }

    @Override
    public void save(Student s) {
        String sql = """
            INSERT INTO students(name, cpf, email, birth_date)
            VALUES (?, ?, ?, ?)
            RETURNING id
            """;
        try (Connection c = db.openConnection();            // <<< ajuste getConnection/openConnection conforme sua classe Database
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getCpf());
            ps.setString(3, s.getEmail());
            if (s.getBirthDate() == null) ps.setNull(4, Types.DATE);
            else ps.setDate(4, Date.valueOf(s.getBirthDate()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) s.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar aluno", e);
        }
    }

    @Override
    public void update(Student s) {
        String sql = """
            UPDATE students
               SET name=?, email=?, birth_date=?, updated_at=NOW()
             WHERE id=?
            """;
        try (Connection c = db.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getEmail());
            if (s.getBirthDate() == null) ps.setNull(3, Types.DATE);
            else ps.setDate(3, Date.valueOf(s.getBirthDate()));
            ps.setLong(4, s.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar aluno", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM students WHERE id=?";
        try (Connection c = db.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate(); // opcional: checar retorno >0
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar aluno", e);
        }
    }

    @Override
    public Optional<Student> findById(Long id) {
        String sql = "SELECT * FROM students WHERE id=?";
        try (Connection c = db.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar por id", e); // <<< propaga causa
        }
    }

    @Override
    public Optional<Student> findByCpf(String cpfDigits) {
        String sql = "SELECT * FROM students WHERE cpf=?";          // <<< era DELETE
        try (Connection c = db.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cpfDigits);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pelo cpf", e);
        }
    }

    @Override
    public List<Student> list(int page, int size) {
        String sql = """
            SELECT * FROM students
             ORDER BY created_at DESC
             LIMIT ? OFFSET ?
            """;
        try (Connection c = db.openConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, size);               // <<< LIMIT = size (antes estava page)
            ps.setInt(2, page * size);        // <<< OFFSET = page * size
            try (ResultSet rs = ps.executeQuery()) {
                List<Student> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar alunos", e);
        }
    }

    private Student map(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String cpf = rs.getString("cpf");
        String email = rs.getString("email");
        Date d = rs.getDate("birth_date");
        LocalDate birth = (d == null ? null : d.toLocalDate());
        return new Student(id, name, cpf, email, birth);
    }
}
