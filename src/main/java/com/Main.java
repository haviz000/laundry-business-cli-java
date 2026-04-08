// File: Main.java
package com;

import com.controller.*;
import com.repository.*;
import com.service.*;
import picocli.CommandLine;
import picocli.CommandLine.*;

@Command(
        name = "laundry",
        mixinStandardHelpOptions = true,
        version = "1.0",
        description = "Aplikasi Manajemen Laundry CLI",
        subcommands = {
                AuthController.class,
                CustomerController.class,
                OrderController.class,
                PaymentController.class,
                ReportController.class
        }
)
public class Main implements Runnable {

    public void run() {
        System.out.println("Gunakan: laundry [auth|customer|order|payment|report] --help");
    }

    public static void main(String[] args) throws Exception {
        // ── 1. Init database & repositories ──
        DatabaseConnection.getInstance(); // trigger pembuatan tabel + seed admin

        UserRepository userRepo = new UserRepository();
        CustomerRepository customerRepo = new CustomerRepository();
        ServiceRepository serviceRepo = new ServiceRepository();
        OrderRepository orderRepo = new OrderRepository();
        PaymentRepository paymentRepo = new PaymentRepository();

        // ── 2. Init services ──
        SessionService sessionService = new SessionService();
        sessionService.load(); // muat sesi dari file (jika ada)

        AuthService authService = new AuthService(userRepo, sessionService);
        CustomerService customerService = new CustomerService(customerRepo);
        OrderService orderService = new OrderService(orderRepo, serviceRepo, paymentRepo);
        PaymentService paymentService = new PaymentService(paymentRepo);
        PenaltyService penaltyService = new PenaltyService(orderRepo);
        ReportService reportService = new ReportService(orderRepo, paymentRepo);

        // ── 3. Inject services ke controllers (via static field) ──
        // Catatan: pada production app, lebih baik pakai DI framework seperti Spring
        AuthController.authService = authService;
        CustomerController.customerService = customerService;
        CustomerController.sessionService = sessionService;
        OrderController.orderService = orderService;
        OrderController.paymentService = paymentService;
        OrderController.penaltyService = penaltyService;
        OrderController.sessionService = sessionService;
        PaymentController.paymentService = paymentService;
        PaymentController.sessionService = sessionService;
        ReportController.reportService = reportService;
        ReportController.sessionService = sessionService;

        // ── 4. Jalankan Picocli ──
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }
}