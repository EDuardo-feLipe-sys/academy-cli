package service;

import cli.Menu;
import config.Database;
import repository.JdbcStudentRepository;
import repository.StudentRepository;

public class Main {
    public static void main(String[] args) {
        Database db = new Database();
        StudentRepository repo = new JdbcStudentRepository(db);
        StudentService service = new StudentService(repo);
        new Menu(service).start();
    }
}

