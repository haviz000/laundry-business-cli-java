package com.service;

import com.entity.Payment;
import com.entity.enums.PaymentMethod;
import com.entity.enums.PaymentStatus;
import com.repository.PaymentRepository;

import java.sql.SQLException;
import java.util.Optional;

public class PaymentService {
    private final PaymentRepository repo;

    public PaymentService(PaymentRepository repo) {
        this.repo = repo;
    }

    public Optional<Payment> getByOrderId(String orderId) throws SQLException {
        return repo.findByOrderId(orderId);
    }

    public void pay(String orderId, double amount,
                    PaymentMethod method) throws SQLException {
        Payment payment = repo.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Tagihan tidak ditemukan untuk order: " + orderId));

        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Order ini sudah dibayar!");
        }
        if (amount < payment.getTotalBill()) {
            throw new RuntimeException(String.format(
                    "Pembayaran kurang! Tagihan: Rp %.0f, dibayar: Rp %.0f",
                    payment.getTotalBill(), amount));
        }

        repo.markPaid(orderId, amount, method);
    }
}
