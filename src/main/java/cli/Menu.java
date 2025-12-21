package cli;

import domain.Student;
import service.StudentService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Menu {
    private final StudentService service;
    private final Scanner in = new Scanner(System.in);

    public Menu(StudentService service) {
        this.service = service;
    }

    public void start() {
        for (;;) {
            System.out.println("""
        === Students CLI ===
        1) Cadastrar
        2) Listar
        3) Buscar por ID
        4) Atualizar (nome/email)
        5) Remover
        0) Sair
        """);
            System.out.print("Opção: ");
            String op = in.nextLine().trim();

            try {
                switch (op) {
                    case "1":
                        cadastrar();
                        pause();
                        continue;
                    case "2":
                        listar();
                        pause();
                        continue;
                    case "3":
                        buscar();
                        pause();
                        continue;
                    case "4":
                        atualizar();
                        pause();
                        continue;
                    case "5":
                        remover();
                        pause();
                        continue;
                    case "0":
                        if (confirm("Deseja realmente sair? (s/n): ")) {
                            System.out.println("Encerrando...");
                            break;
                        } else {
                            continue;
                        }
                    default:
                        System.out.println("Opção inválida.");
                        pause();
                }
            } catch (Exception e) {
                System.out.println("[ERRO] " + e.getMessage());
                if (e.getCause() != null) System.out.println("[CAUSA] " + e.getCause().getMessage());
                pause();
            }
        }
    }

    private void cadastrar() {
        System.out.print("Nome: ");
        String name = in.nextLine();

        System.out.print("CPF: ");
        String cpf = in.nextLine();

        System.out.print("Email: ");
        String email = in.nextLine();

        System.out.print("Data de nascimento (YYYY-MM-DD, opcional): ");
        String dn = in.nextLine().trim();

        LocalDate birth = null;
        if (!dn.isEmpty()) {
            try {
                birth = LocalDate.parse(dn);
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("Data inválida. Use o formato YYYY-MM-DD.");
            }
        }
        Student s = Student.newStudent(name, cpf, email, birth);
        service.create(s);
        System.out.println("=> Criado: " + s);
    }

    private void listar() {
        int p = readInt("página (0..): ", 0);
        int s = readInt("tamanho (1..100): ", 10);
        var list = service.list(p, s);
        if (list.isEmpty()) {
            System.out.println("(sem resultados nesta página)");
        } else {
            list.forEach(System.out::println);
        }
    }

    private void buscar() {
        long id = readLong("id: ");
        System.out.println(service.find(id));
    }

    private void atualizar() {
        long id = readLong("id: ");
        System.out.print("novo nome (vazio mantém): ");
        String name = in.nextLine();
        System.out.print("novo email (vazio mantém): ");
        String email = in.nextLine();
        System.out.println("=> Atualizado: " + service.update(id, name, email));
    }

    private void remover() {
        long id = readLong("id: ");
        service.delete(id);
        System.out.println("=> Removido.");
    }

    private boolean confirm(String prompt) {
        System.out.print(prompt);
        String ans = in.nextLine().trim().toLowerCase();
        return ans.startsWith("s") || ans.equals("y") || ans.equals("yes");
    }

    private void pause() {
        System.out.print("(Enter para continuar) ");
        in.nextLine();
    }

    private int readInt(String prompt, int defaultVal) {
        System.out.print(prompt);
        String s = in.nextLine().trim();
        if (s.isEmpty()) return defaultVal;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Número inválido.");
        }
    }

    private long readLong(String prompt) {
        System.out.print(prompt);
        String s = in.nextLine().trim();
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Número inválido.");
        }
    }
}
