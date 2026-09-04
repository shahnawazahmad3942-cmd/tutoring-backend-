package com.example.backend.config;

/* Creates the initial ADMIN user at startup if one does not already exist.
Credentials come from environment variables, never from the codebase. */

import com.example.backend.entity.User;
import com.example.backend.enums.Role;
import com.example.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner{

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminSeeder(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        @Value("${admin.email}") String adminEmail,
        @Value("${admin.password}") String adminPassword
    ){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args){
        if(userRepository.existsByEmail(adminEmail)){
            log.info("Admin user already exists, skipping seeding");
            return;
        }

        User admin = new User();
        admin.setName("Administrator");
        admin.setEmail(adminEmail);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        admin.setEmailVerified(true);

        userRepository.save(admin);
        log.info("Seeded initial admin user : {}", adminEmail);

    }
    
}
