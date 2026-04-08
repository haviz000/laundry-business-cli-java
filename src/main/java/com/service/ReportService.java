package com.service;

import com.entity.Order;
import com.entity.Payment;
import com.entity.enums.PaymentStatus;
import com.repository.OrderRepository;
import com.repository.PaymentRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ReportService {
    private final OrderRepository orderRepo;
    private final PaymentRepository paymentRepo;

    public ReportService(OrderRepository orderRepo, PaymentRepository paymentRepo) {
        this.orderRepo = orderRepo;
        this.paymentRepo = paymentRepo;
    }

    public List<Order> dailyOrders() throws SQLException {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        return orderRepo.findByDateRange(startOfDay, endOfDay);
    }

    // Laporan order bulan ini
    public List<Order> monthlyOrders() throws SQLException {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);
        return orderRepo.findByDateRange(startOfMonth, endOfMonth);
    }

    // Total pendapatan (dari payment yang sudah PAID)
    public double totalRevenue() throws SQLException {
        return orderRepo.findAll().stream()
                .map(o -> {
                    try {
                        return paymentRepo.findByOrderId(o.getId());
                    } catch (SQLException e) {
                        return Optional.<Payment>empty();
                    }
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .mapToDouble(Payment::getPaidAmount)
                .sum();
    }

    public Map<String, Long> topCustomers() throws SQLException {
        return orderRepo.findAll().stream()
                .collect(Collectors.groupingBy(Order::getCustomerId, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

}
