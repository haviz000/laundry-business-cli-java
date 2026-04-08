package com.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_PATH = "laundry.db";
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
        createTables();
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void createTables() throws SQLException {
        Statement stmt = connection.createStatement();

        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id TEXT PRIMARY KEY,
                        username TEXT UNIQUE NOT NULL,
                        password_hash TEXT NOT NULL,
                        role        TEXT NOT NULL,
                        created_at  TEXT NOT NULL
                    )
                """);
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS customers (
                        id         TEXT PRIMARY KEY,
                        name       TEXT NOT NULL,
                        phone      TEXT,
                        address    TEXT,
                        created_at TEXT NOT NULL
                    )
                """);

        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS laundry_services (
                        id            TEXT PRIMARY KEY,
                        name          TEXT NOT NULL,
                        price_per_kg  REAL NOT NULL,
                        estimate_days INTEGER NOT NULL
                    )
                """);

        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS orders (
                        id             TEXT PRIMARY KEY,
                        customer_id    TEXT NOT NULL,
                        service_id     TEXT NOT NULL,
                        weight         REAL NOT NULL,
                        total_price    REAL NOT NULL,
                        status         TEXT NOT NULL,
                        created_at     TEXT NOT NULL,
                        estimated_done TEXT,
                        finished_at    TEXT
                    )
                """);

        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS payments (
                        id          TEXT PRIMARY KEY,
                        order_id    TEXT NOT NULL,
                        total_bill  REAL NOT NULL,
                        paid_amount REAL DEFAULT 0,
                        method      TEXT,
                        status      TEXT NOT NULL DEFAULT 'UNPAID',
                        paid_at     TEXT
                    )
                """);

        stmt.close();
        seedAdminUser();
    }

    private void seedAdminUser() throws SQLException {
        var check = connection.prepareStatement("SELECT COUNT(*) FROM users");
        var rs = check.executeQuery();

        if (rs.getInt(1) == 0) {
            String hash = org.mindrot.jbcrypt.BCrypt.hashpw("admin123", org.mindrot.jbcrypt.BCrypt.gensalt());
            var ins = connection.prepareStatement(
                    "INSERT INTO users VALUES (?, ?, ?, ?, ?)");
            ins.setString(1, java.util.UUID.randomUUID().toString());
            ins.setString(2, "admin");
            ins.setString(3, hash);
            ins.setString(4, "ADMIN");
            ins.setString(5, java.time.LocalDateTime.now().toString());
            ins.executeUpdate();
            System.out.println("[INFO] Default admin dibuat: username=admin, password=admin123");
        }

        rs.close();
        check.close();
    }
}
