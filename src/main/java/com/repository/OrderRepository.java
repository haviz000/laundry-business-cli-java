package com.repository;

import com.entity.Order;
import com.entity.enums.OrderStatus;

import java.sql.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderRepository {
    private final Connection conn;

    public OrderRepository() throws SQLException {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public void save(Order o) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("""
                    INSERT INTO orders
                    (id,customer_id,service_id,weight,total_price,status,created_at,estimated_done)
                    VALUES (?,?,?,?,?,?,?,?)
                """);
        ps.setString(1, o.getId());
        ps.setString(2, o.getCustomerId());
        ps.setString(3, o.getServiceId());
        ps.setDouble(4, o.getWeight());
        ps.setDouble(5, o.getTotalPrice());
        ps.setString(6, o.getStatus().name());
        ps.setString(7, o.getCreatedAt().toString());
        ps.setString(8, o.getEstimatedDone() != null ? o.getEstimatedDone().toString() : null);
        ps.executeUpdate();
    }

    public List<Order> findAll() throws SQLException {
        List<Order> list = new ArrayList<>();
        ResultSet rs = conn.createStatement()
                .executeQuery("SELECT * FROM orders ORDER BY created_at DESC");
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    public Optional<Order> findById(String id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM orders WHERE id=?");
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return Optional.of(mapRow(rs));
        return Optional.empty();
    }

    public void updateStatus(String id, OrderStatus status) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE orders SET status=? WHERE id=?");
        ps.setString(1, status.name());
        ps.setString(2, id);
        ps.executeUpdate();
    }

    public void setFinishedAt(String id, LocalDateTime time) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE orders SET finished_at=? WHERE id=?");
        ps.setString(1, time.toString());
        ps.setString(2, id);
        ps.executeUpdate();
    }

    public List<Order> findByDateRange(LocalDateTime from, LocalDateTime to) throws SQLException {
        List<Order> list = new ArrayList<>();
        PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM orders WHERE created_at >= ? AND created_at <= ?");
        ps.setString(1, from.toString());
        ps.setString(2, to.toString());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        Order o = new Order(
                rs.getString("id"),
                rs.getString("customer_id"),
                rs.getString("service_id"),
                rs.getDouble("weight"),
                rs.getDouble("total_price"),
                OrderStatus.valueOf(rs.getString("status")),
                LocalDateTime.parse(rs.getString("created_at")),
                rs.getString("estimated_done") != null
                        ? LocalDateTime.parse(rs.getString("estimated_done")) : null
        );
        String finAt = rs.getString("finished_at");
        if (finAt != null) o.setFinishedAt(LocalDateTime.parse(finAt));
        return o;
    }
}
