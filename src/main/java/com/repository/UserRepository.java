package com.repository;
import com.entity.User;
import com.entity.enums.Role;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class UserRepository {
    private final Connection conn;

    public UserRepository() throws SQLException {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM users WHERE username = ?");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    public void save(User user) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users (id, username, password_hash, role, created_at) VALUES (?,?,?,?,?)");
        ps.setString(1, user.getId() != null ? user.getId() : UUID.randomUUID().toString());
        ps.setString(2, user.getUsername());
        ps.setString(3, user.getPasswordHash());
        ps.setString(4, user.getRole().name());
        ps.setString(5, LocalDateTime.now().toString());
        ps.executeUpdate();
    }

    // Mapping dari ResultSet (baris database) ke objek User
    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                Role.valueOf(rs.getString("role")),
                LocalDateTime.parse(rs.getString("created_at"))
        );
    }
}