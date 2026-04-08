// File: controller/ReportController.java
package com.controller;

import com.entity.Order;
import com.entity.enums.Role;
import com.service.ReportService;
import com.service.SessionService;
import picocli.CommandLine.*;

import java.util.List;
import java.util.Map;

@Command(name = "report", mixinStandardHelpOptions = true,
        subcommands = {
                ReportController.Daily.class,
                ReportController.Monthly.class,
                ReportController.Revenue.class,
                ReportController.TopCustomer.class
        })
public class ReportController implements Runnable {
    public void run() { System.out.println("Gunakan: report [daily|monthly|revenue|top-customer]"); }

    public static ReportService  reportService;
    public static SessionService sessionService;

    private static boolean checkAccess() {
        if (!sessionService.hasRole(Role.ADMIN, Role.OWNER)) {
            System.out.println("Akses ditolak! Hanya ADMIN/OWNER.");
            return false;
        }
        return true;
    }

    @Command(name = "daily")
    static class Daily implements Runnable {
        public void run() {
            if (!checkAccess()) return;
            try {
                List<Order> orders = reportService.dailyOrders();
                System.out.println("=== Laporan Harian ===");
                System.out.println("Total order hari ini: " + orders.size());
                double total = orders.stream().mapToDouble(Order::getTotalPrice).sum();
                System.out.printf("Total nilai         : Rp %.0f%n", total);
            } catch (Exception e) { System.err.println(e.getMessage()); }
        }
    }

    @Command(name = "monthly")
    static class Monthly implements Runnable {
        public void run() {
            if (!checkAccess()) return;
            try {
                List<Order> orders = reportService.monthlyOrders();
                System.out.println("=== Laporan Bulanan ===");
                System.out.println("Total order bulan ini: " + orders.size());
                double total = orders.stream().mapToDouble(Order::getTotalPrice).sum();
                System.out.printf("Total nilai          : Rp %.0f%n", total);
            } catch (Exception e) { System.err.println(e.getMessage()); }
        }
    }

    @Command(name = "revenue")
    static class Revenue implements Runnable {
        public void run() {
            if (!checkAccess()) return;
            try {
                double revenue = reportService.totalRevenue();
                System.out.printf("Total pendapatan (dibayar): Rp %.0f%n", revenue);
            } catch (Exception e) { System.err.println(e.getMessage()); }
        }
    }

    @Command(name = "top-customer")
    static class TopCustomer implements Runnable {
        public void run() {
            if (!checkAccess()) return;
            try {
                Map<String, Long> top = reportService.topCustomers();
                System.out.println("=== Top 5 Customer ===");
                top.forEach((id, count) ->
                        System.out.printf("Customer ID: %-36s | Order: %d%n", id, count));
            } catch (Exception e) { System.err.println(e.getMessage()); }
        }
    }
}