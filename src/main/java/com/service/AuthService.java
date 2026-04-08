package com.service;

import com.entity.User;
import com.repository.UserRepository;
import com.util.PasswordUtil;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;
    private final SessionService sessionService;

    public AuthService(UserRepository userRepository, SessionService sessionService) {
        this.sessionService = sessionService;
        this.userRepository = userRepository;
    }

    public boolean login(String username, String password) throws SQLException {
        Optional<User> opt = userRepository.findByUsername(username);
        if (opt.isEmpty()) return false;

        User user = opt.get();
        // Verifikasi password menggunakan BCrypt
        if (!PasswordUtil.verify(password, user.getPasswordHash())) return false;

        // Simpan sesi ke file
        sessionService.save(user.getId(), user.getUsername(), user.getRole());
        return true;
    }

    public void logout() {
        sessionService.clear();
    }
}
