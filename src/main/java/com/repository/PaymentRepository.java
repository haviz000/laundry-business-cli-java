package com.repository;

import com.entity.Payment;
import com.entity.enums.PaymentMethod;
import com.entity.enums.PaymentStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class PaymentRepository {
    private final Connection conn;

    public PaymentRepository() throws SQLException {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public void save(Payment p) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO payments (id,order_id,total_bill,status) VALUES (?,?,?,?)");
        ps.setString(1, p.getId());
        ps.setString(2, p.getOrderId());
        ps.setDouble(3, p.getTotalBill());
        ps.setString(4, PaymentStatus.UNPAID.name());
        ps.executeUpdate();
    }

    public Optional<Payment> findByOrderId(String orderId) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM payments WHERE order_id=?");
        ps.setString(1, orderId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return Optional.of(mapRow(rs));
        return Optional.empty();
    }

    public void markPaid(String orderId, double amount,
                         PaymentMethod method) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("""
                    UPDATE payments SET
                        paid_amount=?, method=?, status='PAID', paid_at=?
                    WHERE order_id=?
                """);
        ps.setDouble(1, amount);
        ps.setString(2, method.name());
        ps.setString(3, LocalDateTime.now().toString());
        ps.setString(4, orderId);
        ps.executeUpdate();
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment p = new Payment(
                rs.getString("id"),
                rs.getString("order_id"),
                rs.getDouble("total_bill")
        );
        p.setPaidAmount(rs.getDouble("paid_amount"));
        p.setStatus(PaymentStatus.valueOf(rs.getString("status")));
        String method = rs.getString("method");
        if (method != null) p.setMethod(PaymentMethod.valueOf(method));
        String paidAt = rs.getString("paid_at");
        if (paidAt != null) p.setPaidAt(LocalDateTime.parse(paidAt));
        return p;
    }
}
