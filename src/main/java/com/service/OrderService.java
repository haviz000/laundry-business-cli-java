package com.service;

import com.entity.LaundryService;
import com.entity.Order;
import com.entity.Payment;
import com.entity.enums.OrderStatus;
import com.repository.OrderRepository;
import com.repository.PaymentRepository;
import com.repository.ServiceRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderService {
    private final OrderRepository orderRepo;
    private final ServiceRepository serviceRepo;
    private final PaymentRepository paymentRepo;

    public OrderService(OrderRepository orderRepo,
                        ServiceRepository serviceRepo,
                        PaymentRepository paymentRepo) {
        this.orderRepo = orderRepo;
        this.serviceRepo = serviceRepo;
        this.paymentRepo = paymentRepo;
    }

    public Order create(String customerId, String serviceId,
                        double weight) throws SQLException {
        // Ambil data service untuk hitung harga
        LaundryService svc = serviceRepo.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service tidak ditemukan"));

        double totalPrice = weight * svc.getPricePerKg();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime estDone = now.plusDays(svc.getEstimateDays());

        Order order = new Order(
                UUID.randomUUID().toString(), customerId, serviceId,
                weight, totalPrice, OrderStatus.RECEIVED, now, estDone);
        orderRepo.save(order);

        // Otomatis buat record payment dengan status UNPAID
        Payment payment = new Payment(
                UUID.randomUUID().toString(), order.getId(), totalPrice);
        paymentRepo.save(payment);

        return order;
    }

    public List<Order> getAll() throws SQLException {
        return orderRepo.findAll();
    }

    public Optional<Order> getById(String id) throws SQLException {
        return orderRepo.findById(id);
    }

    public void advanceStatus(String orderId) throws SQLException {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order tidak ditemukan"));

        if (order.getStatus() == OrderStatus.PICKED_UP) {
            throw new RuntimeException("Order sudah selesai (PICKED_UP)");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order sudah dibatalkan");
        }

        OrderStatus next = order.getStatus().next();
        orderRepo.updateStatus(orderId, next);

        // Jika status menjadi DONE, catat waktu selesai
        if (next == OrderStatus.DONE) {
            orderRepo.setFinishedAt(orderId, LocalDateTime.now());
        }
    }

    public void cancel(String orderId) throws SQLException {
        orderRepo.updateStatus(orderId, OrderStatus.CANCELLED);
    }

}
