package com.service;

import com.entity.enums.Role;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;

public class SessionService {
    private static final String SESSION_DIR = System.getProperty("user.home") + "/.laundry";
    private static final String SESSION_FILE = SESSION_DIR + "/session.json";
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                    (com.google.gson.JsonDeserializer<LocalDateTime>)
                            (json, t, ctx) -> LocalDateTime.parse(json.getAsString()))
            .registerTypeAdapter(LocalDateTime.class,
                    (com.google.gson.JsonSerializer<LocalDateTime>)
                            (src, t, ctx) -> new com.google.gson.JsonPrimitive(src.toString()))
            .create();

    public static class Session {
        public String userId;
        public String username;
        public String role;
        public LocalDateTime loginAt;
        public LocalDateTime expiresAt;
    }

    private Session currentSession;

    public void load() {
        try {
            File file = new File(SESSION_FILE);
            if (!file.exists()) return;
            String json = Files.readString(file.toPath());
            Session s = GSON.fromJson(json, Session.class);

            if (s != null && s.expiresAt.isAfter(LocalDateTime.now())) {
                this.currentSession = s;
            } else {
                clear();
            }
        } catch (Exception e) {
            // invalid session
        }
    }

    public void save(String userId, String username, Role role) {
        Session s = new Session();
        s.userId = userId;
        s.username = username;
        s.role = role.name();
        s.loginAt = LocalDateTime.now();
        s.expiresAt = LocalDateTime.now().plusHours(24);
        this.currentSession = s;

        try {
            Files.createDirectories(Path.of(SESSION_DIR));
            Files.writeString(Path.of(SESSION_FILE), GSON.toJson(s));
        } catch (IOException e) {
            System.err.println("Gagal menyimpan sesi: " + e.getMessage());
        }
    }

    public void clear() {
        this.currentSession = null;
        try { Files.deleteIfExists(Path.of(SESSION_FILE)); } catch (IOException ignored) {}
    }

    public boolean isLoggedIn()  { return currentSession != null; }
    public Session getSession()  { return currentSession; }

    public boolean hasRole(Role... allowedRoles) {
        if (!isLoggedIn()) return false;
        Role userRole = Role.valueOf(currentSession.role);
        for (Role r : allowedRoles) {
            if (r == userRole) return true;
        }
        return false;
    }

}
