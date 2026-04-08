package com.service;

import com.entity.Order;
import com.entity.Penalty;
import com.entity.enums.OrderStatus;
import com.repository.OrderRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

public class PenaltyService {
    private static final int GRACE_PERIOD_DAYS = 2;
    private static final double PENALTY_PER_DAY = 2000.0;

    private final OrderRepository orderRepo;

    public PenaltyService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    public Optional<Penalty> check(String orderId) throws SQLException {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order tidak ditemukan"));

        // Denda hanya berlaku jika order sudah DONE tapi belum diambil
        if (order.getStatus() != OrderStatus.DONE) return Optional.empty();
        if (order.getFinishedAt() == null)          return Optional.empty();

        LocalDate finishedDate = order.getFinishedAt().toLocalDate();
        LocalDate deadlineDate = finishedDate.plusDays(GRACE_PERIOD_DAYS);
        LocalDate today        = LocalDate.now();

        long lateDays = ChronoUnit.DAYS.between(deadlineDate, today);
        if (lateDays <= 0) return Optional.empty();

        double amount = lateDays * PENALTY_PER_DAY;
        Penalty penalty = new Penalty(
                UUID.randomUUID().toString(), orderId, (int) lateDays, amount);
        return Optional.of(penalty);
    }
}
