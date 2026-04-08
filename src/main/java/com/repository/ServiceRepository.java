package com.repository;

import com.entity.LaundryService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceRepository {
    private final Connection conn;

    public ServiceRepository() throws SQLException {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public void save(LaundryService s) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("INSERT INTO laundry_services (id,name,price_per_kg,estimate_days) VALUES (?,?,?,?)");
        ps.setString(1, s.getId());
        ps.setString(2, s.getName());
        ps.setDouble(3, s.getPricePerKg());
        ps.setInt(4, s.getEstimateDays());
        ps.executeUpdate();
    }

    public List<LaundryService> findAll() throws SQLException {
        List<LaundryService> list = new ArrayList<>();
        ResultSet rs = conn.createStatement()
                .executeQuery("SELECT * FROM laundry_services");
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    public Optional<LaundryService> findById(String id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM laundry_services WHERE id=?");
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return Optional.of(mapRow(rs));
        return Optional.empty();
    }

    public void update(LaundryService s) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE laundry_services SET name=?,price_per_kg=?,estimate_days=? WHERE id=?");
        ps.setString(1, s.getName());
        ps.setDouble(2, s.getPricePerKg());
        ps.setInt(3, s.getEstimateDays());
        ps.setString(4, s.getId());
        ps.executeUpdate();
    }

    public void delete(String id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM laundry_services WHERE id=?");
        ps.setString(1, id);
        ps.executeUpdate();
    }

    private LaundryService mapRow(ResultSet rs) throws SQLException {
        return new LaundryService(
                rs.getString("id"), rs.getString("name"),
                rs.getDouble("price_per_kg"), rs.getInt("estimate_days")
        );
    }

}
