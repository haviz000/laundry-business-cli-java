// File: controller/PaymentController.java
package com.controller;

import com.entity.enums.PaymentMethod;
import com.entity.enums.Role;
import com.service.PaymentService;
import com.service.SessionService;
import picocli.CommandLine.*;

import java.util.Scanner;

@Command(name = "payment", mixinStandardHelpOptions = true,
        subcommands = {
                PaymentController.Pay.class,
                PaymentController.Detail.class
        })
public class PaymentController implements Runnable {
    public void run() { System.out.println("Gunakan: payment [pay|detail]"); }

    public static PaymentService paymentService;
    public static SessionService sessionService;

    @Command(name = "pay")
    static class Pay implements Runnable {
        @Parameters(index = "0", description = "ID Order") String orderId;
        public void run() {
            if (!sessionService.hasRole(Role.ADMIN, Role.STAFF)) {
                System.out.println("Akses ditolak!"); return;
            }
            try {
                paymentService.getByOrderId(orderId).ifPresentOrElse(p -> {
                    System.out.printf("Tagihan : Rp %.0f%n", p.getTotalBill());
                    System.out.println("Metode  : 1=CASH  2=TRANSFER  3=E_WALLET");
                    Scanner sc = new Scanner(System.in);
                    System.out.print("Pilih metode (1-3): "); int m = sc.nextInt();
                    System.out.print("Jumlah bayar      : "); double amt = sc.nextDouble();
                    PaymentMethod method = switch (m) {
                        case 2 -> PaymentMethod.TRANSFER;
                        case 3 -> PaymentMethod.E_WALLET;
                        default -> PaymentMethod.CASH;
                    };
                    try {
                        paymentService.pay(orderId, amt, method);
                        double kembalian = amt - p.getTotalBill();
                        System.out.printf("Pembayaran berhasil! Kembalian: Rp %.0f%n", kembalian);
                    } catch (Exception e) { System.err.println(e.getMessage()); }
                }, () -> System.out.println("Tagihan tidak ditemukan."));
            } catch (Exception e) { System.err.println(e.getMessage()); }
        }
    }

    @Command(name = "detail")
    static class Detail implements Runnable {
        @Parameters(index = "0") String orderId;
        public void run() {
            if (!sessionService.isLoggedIn()) return;
            try {
                paymentService.getByOrderId(orderId).ifPresentOrElse(p -> {
                    System.out.println("Order ID    : " + p.getOrderId());
                    System.out.printf ("Tagihan     : Rp %.0f%n", p.getTotalBill());
                    System.out.printf ("Dibayar     : Rp %.0f%n", p.getPaidAmount());
                    System.out.println("Metode      : " + p.getMethod());
                    System.out.println("Status      : " + p.getStatus());
                    System.out.println("Waktu bayar : " + p.getPaidAt());
                }, () -> System.out.println("Data pembayaran tidak ditemukan."));
            } catch (Exception e) { System.err.println(e.getMessage()); }
        }
    }
}