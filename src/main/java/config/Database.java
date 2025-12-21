package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private final String url;
    private final String username;
    private final String pass;

    public Database() {
        this.url = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://127.0.0.1:5432/postgres");
        this.username = System.getenv().getOrDefault("DB_USERNAME", "postgres");
        this.pass = System.getenv().getOrDefault("DB_PASS", "222324");
    }

    public Connection openConnection() throws SQLException {
        return DriverManager.getConnection(url, username, pass);
    }

}
