// File: controller/OrderController.java
package com.controller;

import com.entity.*;
import com.entity.enums.Role;
import com.service.*;
import picocli.CommandLine.*;

import java.util.List;
import java.util.Scanner;

@Command(name = "order", mixinStandardHelpOptions = true,
        subcommands = {
                OrderController.Create.class,
                OrderController.ListAll.class,
                OrderController.Detail.class,
                OrderController.UpdateStatus.class,
                OrderController.Cancel.class,
                OrderController.Pickup.class
        })
public class OrderController implements Runnable {
    public void run() { System.out.println("Gunakan: order [create|list|detail|update-status|cancel|pickup]"); }

    public static OrderService   orderService;
    public static PaymentService paymentService;
    public static PenaltyService penaltyService;
    public static SessionService sessionService;

    @Command(name = "create")
    static class Create implements Runnable {
        public void run() {
            if (!sessionService.hasRole(Role.ADMIN, Role.STAFF)) {
                System.out.println("Akses ditolak!"); return;
            }
            Scanner sc = new Scanner(System.in);
            System.out.print("ID Customer  : "); String customerId = sc.nextLine();
            System.out.print("ID Service   : "); String serviceId  = sc.nextLine();
            System.out.print("Berat (kg)   : "); double weight     = sc.nextDouble();
            try {
                Order order = orderService.create(customerId, serviceId, weight);
                System.out.printf("Order dibuat! ID: %s%n", order.getId());
                System.out.printf("Total harga : Rp %.0f%n", order.getTotalPrice());
                System.out.printf("Est. selesai: %s%n", order.getEstimatedDone().toLocalDate());
            } catch (Exception e) { System.err.println(e.getMessage()); }
        }
    }

    @Command(name = "list")
    static class ListAll implements Runnable {
        public void run() {
            if (!sessionService.isLoggedIn()) { System.out.println("Silakan login."); return; }
            try {
                List<Order> orders = orderService.getAll();
                System.out.printf("%-36s %-10s %-10s %-12s%n",
                        "ID", "Berat", "Total", "Status");
                System.out.println("-".repeat(72));
                for (Order o : orders) {
                    System.out.printf("%-36s %-10.1f %-10.0f %-12s%n",
                            o.getId(), o.getWeight(), o.getTotalPrice(), o.getStatus());
                }
            } catch (Exception e) { System.err.println(e.getMessage()); }
        }
    }

    @Command(name = "detail")
    static class Detail implements Runnable {
        @Parameters(index = "0") String id;
        public void run() {
            if (!sessionService.isLoggedIn()) return;
            try {
                orderService.getById(id).ifPresentOrElse(o -> {
                    System.out.println("ID          : " + o.getId());
                    System.out.println("Customer    : " + o.getCustomerId());
                    System.out.println("Service     : " + o.getServiceId());
                    System.out.printf ("Berat       : %.1f kg%n", o.getWeight());
                    System.out.printf ("Total       : Rp %.0f%n", o.getTotalPrice());
                    System.out.println("Status      : " + o.getStatus());
                    System.out.println("Dibuat      : " + o.getCreatedAt());
                    System.out.println("Est. selesai: " + o.getEstimatedDone());
                }, () -> System.out.println("Order tidak ditemukan."));
            } catch (Exception e) { System.err.println(e.getMessage()); }
        }
    }

    @Command(name = "update-status")
    static class UpdateStatus implements Runnable {
        @Parameters(index = "0") String id;
        public void run() {
            if (!sessionService.hasRole(Role.ADMIN, Role.STAFF)) {
                System.out.println("Akses ditolak!"); return;
            }
            try {
                orderService.advanceStatus(id);
                System.out.println("Status order berhasil dimajukan.");
            } catch (Exception e) { System.err.println(e.getMessage()); }
        }
    }

    @Command(name = "cancel")
    static class Cancel implements Runnable {
        @Parameters(index = "0") String id;
        public void run() {
            if (!sessionService.hasRole(Role.ADMIN, Role.STAFF)) {
                System.out.println("Akses ditolak!"); return;
            }
            try {
                orderService.cancel(id);
                System.out.println("Order dibatalkan.");
            } catch (Exception e) { System.err.println(e.getMessage()); }
        }
    }

    @Command(name = "pickup")
    static class Pickup implements Runnable {
        @Parameters(index = "0") String id;
        public void run() {
            if (!sessionService.hasRole(Role.ADMIN, Role.STAFF)) {
                System.out.println("Akses ditolak!"); return;
            }
            try {
                // Cek denda dulu
                penaltyService.check(id).ifPresent(p ->
                        System.out.printf("[PERINGATAN] Ada denda: %d hari terlambat, " +
                                "Rp %.0f%n", p.getDaysLate(), p.getAmount()));

                // Cek apakah sudah bayar
                paymentService.getByOrderId(id).ifPresent(p -> {
                    if (p.getStatus().name().equals("UNPAID")) {
                        throw new RuntimeException("Belum dibayar! Selesaikan pembayaran dulu.");
                    }
                });

                orderService.advanceStatus(id); // maju ke PICKED_UP
                System.out.println("Order berhasil diambil. Terima kasih!");
            } catch (Exception e) { System.err.println(e.getMessage()); }
        }
    }
}