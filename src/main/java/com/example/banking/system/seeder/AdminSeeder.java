// seeder/AdminSeeder.java
package com.example.banking.system.seeder;

import com.example.banking.system.model.User;
import com.example.banking.system.model.enums.Role;
import com.example.banking.system.model.enums.Status;
import com.example.banking.system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements ApplicationRunner {

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        boolean adminExists = userRepository.findByRole(Role.ADMIN).isPresent();

        if (!adminExists) {
            User admin = new User();
            admin.setFirstName("Adrian");
            admin.setMiddleName(null);
            admin.setLastName("Villanueva");
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setStatus(Status.ACTIVE);

            userRepository.save(admin);
            System.out.println("Admin seeder: Admin account created successfully!");
        } else {
            System.out.println("Admin seeder: Admin account already exists, skipping...");
        }
    }
}