package com.terraguard.quakexit.config;

import com.terraguard.quakexit.common.enums.DomainEnums.Role;
import com.terraguard.quakexit.iam.entity.User;
import com.terraguard.quakexit.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminUserInitializer {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initializeAdminUser(
            @Value("${app.admin.email}") String email,
            @Value("${app.admin.password}") String password,
            @Value("${app.admin.full-name}") String fullName) {
        return args -> {
            User admin = users.findByEmailIgnoreCase(email).orElseGet(User::new);
            admin.setFullName(fullName);
            admin.setEmail(email.toLowerCase());
            admin.setPasswordHash(passwordEncoder.encode(password));
            admin.setRole(Role.SYSTEM_ADMIN);
            admin.setEnabled(true);
            users.save(admin);
        };
    }
}