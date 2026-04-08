package com.repository;

import com.entity.Customer;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerRepository {
    private final Connection conn;

    public CustomerRepository() throws SQLException {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public void save(Customer c) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("INSERT INTO customers (id,name,phone,address,created_at) VALUES (?,?,?,?,?)");
        ps.setString(1, c.getId());
        ps.setString(2, c.getName());
        ps.setString(3, c.getPhone());
        ps.setString(4, c.getAddress());
        ps.setString(5, c.getCreatedAt().toString());
        ps.executeUpdate();
    }

    public List<Customer> findAll() throws SQLException {
        List<Customer> list = new ArrayList<>();
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM customers ORDER BY created_at DESC");
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    public Optional<Customer> findById(String id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM customers WHERE id=?");
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return Optional.of(mapRow(rs));
        return Optional.empty();
    }

    public void update(Customer c) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("UPDATE customers SET name=?, phone=?, WHERE id=?");
        ps.setString(1, c.getName());
        ps.setString(2, c.getPhone());
        ps.setString(3, c.getAddress());
        ps.setString(4, c.getId());
        ps.executeUpdate();
    }

    public void delete(String id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM customers WHERE id=?");
        ps.setString(1, id);
        ps.executeUpdate();
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getString("id"), rs.getString("name"),
                rs.getString("phone"), rs.getString("address"),
                LocalDateTime.parse(rs.getString("created_at"))
        );
    }

}
