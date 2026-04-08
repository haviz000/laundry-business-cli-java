// File: controller/CustomerController.java
package com.controller;

import com.entity.Customer;
import com.entity.enums.Role;
import com.service.CustomerService;
import com.service.SessionService;
import picocli.CommandLine.*;

import java.util.List;
import java.util.Scanner;

@Command(name = "customer", mixinStandardHelpOptions = true,
        subcommands = {
                CustomerController.Add.class,
                CustomerController.ListAll.class,
                CustomerController.Detail.class,
                CustomerController.Update.class,
                CustomerController.Delete.class
        })
public class CustomerController implements Runnable {
    public void run() {
        System.out.println("Gunakan: customer [add|list|detail|update|delete]");
    }

    // Static services diisi dari Main
    public static CustomerService customerService;
    public static SessionService sessionService;

    // ---- Sub-commands ----

    @Command(name = "add", description = "Tambah customer baru")
    static class Add implements Runnable {
        public void run() {
            if (!sessionService.hasRole(Role.ADMIN, Role.STAFF)) {
                System.out.println("Akses ditolak!"); return;
            }
            Scanner sc = new Scanner(System.in);
            System.out.print("Nama    : "); String name    = sc.nextLine();
            System.out.print("Telepon : "); String phone   = sc.nextLine();
            System.out.print("Alamat  : "); String address = sc.nextLine();
            try {
                Customer c = customerService.add(name, phone, address);
                System.out.println("Customer ditambahkan. ID: " + c.getId());
            } catch (Exception e) { System.err.println(e.getMessage()); }
        }
    }

    @Command(name = "list", description = "Tampilkan semua customer")
    static class ListAll implements Runnable {
        public void run() {
            if (!sessionService.isLoggedIn()) { System.out.println("Silakan login dulu."); return; }
            try {
                List<Customer> customers = customerService.getAll();
                if (customers.isEmpty()) { System.out.println("Belum ada customer."); return; }
                System.out.printf("%-36s %-20s %-15s%n", "ID", "Nama", "Telepon");
                System.out.println("-".repeat(75));
                for (Customer c : customers) {
                    System.out.printf("%-36s %-20s %-15s%n",
                            c.getId(), c.getName(), c.getPhone());
                }
            } catch (Exception e) { System.err.println(e.getMessage()); }
        }
    }

    @Command(name = "detail", description = "Detail customer")
    static class Detail implements Runnable {
        @Parameters(index = "0", description = "ID customer") String id;
        public void run() {
            if (!sessionService.isLoggedIn()) { System.out.println("Silakan login dulu."); return; }
            try {
                customerService.getById(id).ifPresentOrElse(c -> {
                    System.out.println("ID      : " + c.getId());
                    System.out.println("Nama    : " + c.getName());
                    System.out.println("Telepon : " + c.getPhone());
                    System.out.println("Alamat  : " + c.getAddress());
                }, () -> System.out.println("Customer tidak ditemukan."));
            } catch (Exception e) { System.err.println(e.getMessage()); }
        }
    }

    @Command(name = "update", description = "Update customer")
    static class Update implements Runnable {
        @Parameters(index = "0") String id;
        public void run() {
            if (!sessionService.hasRole(Role.ADMIN, Role.STAFF)) {
                System.out.println("Akses ditolak!"); return;
            }
            Scanner sc = new Scanner(System.in);
            System.out.print("Nama baru    : "); String name    = sc.nextLine();
            System.out.print("Telepon baru : "); String phone   = sc.nextLine();
            System.out.print("Alamat baru  : "); String address = sc.nextLine();
            try {
                customerService.update(id, name, phone, address);
                System.out.println("Customer berhasil diupdate.");
            } catch (Exception e) { System.err.println(e.getMessage()); }
        }
    }

    @Command(name = "delete", description = "Hapus customer")
    static class Delete implements Runnable {
        @Parameters(index = "0") String id;
        public void run() {
            if (!sessionService.hasRole(Role.ADMIN)) {
                System.out.println("Akses ditolak! Hanya ADMIN."); return;
            }
            try {
                customerService.delete(id);
                System.out.println("Customer dihapus.");
            } catch (Exception e) { System.err.println(e.getMessage()); }
        }
    }
}