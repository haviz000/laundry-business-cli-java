package com.controller;

import com.service.AuthService;
import com.service.SessionService;
import picocli.CommandLine.Command;
import java.util.Scanner;

@Command(name = "auth", subcommands = {
        AuthController.Login.class,
        AuthController.Logout.class,
        AuthController.WhoAmI.class
})
public class AuthController {

    // Injeksi service secara static agar bisa diakses inner class
    public static AuthService authService;
    public static SessionService sessionService;

    // 1. INNER CLASS LOGIN
    @Command(name = "login", description = "Login ke sistem")
    public static class Login implements Runnable {
        public void run() {
            Scanner sc = new Scanner(System.in);
            System.out.print("Username: ");
            String username = sc.nextLine().trim();
            System.out.print("Password: ");
            String password = sc.nextLine().trim();

            try {
                if (authService.login(username, password)) {
                    System.out.println("Login berhasil! Selamat datang, " + username);
                } else {
                    System.out.println("Username atau password salah.");
                }
            } catch (Exception e) {
                System.out.println("masuk kocak");
                e.printStackTrace();
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    // 2. INNER CLASS LOGOUT (Pastikan juga ada @Command dan implements Runnable)
    @Command(name = "logout")
    public static class Logout implements Runnable {
        public void run() {
            sessionService.clear();
            System.out.println("Logout berhasil.");
        }
    }

    // 3. INNER CLASS WHOAMI
    @Command(name = "whoami")
    public static class WhoAmI implements Runnable {
        public void run() {
            // logic whoami...
        }
    }
}