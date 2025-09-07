package com.example.SampleProject.config;

import com.example.SampleProject.model.Permission;
import com.example.SampleProject.model.Role;
import com.example.SampleProject.model.User;
import com.example.SampleProject.repository.PermissionRepository;
import com.example.SampleProject.repository.RoleRepository;
import com.example.SampleProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting database initialization...");

        // Check if data already exists
        if (userRepository.count() > 0) {
            log.info("Database already contains users. Skipping initialization.");
            return;
        }

        if (roleRepository.count() > 0) {
            log.info("Database already contains roles. Skipping initialization.");
            return;
        }

        if (permissionRepository.count() > 0) {
            log.info("Database already contains permissions. Skipping initialization.");
            return;
        }

        try {
            initializePermissions();
            initializeRoles();
            initializeUsers();

            log.info("Database initialization completed successfully!");
        } catch (Exception e) {
            log.error("Error during database initialization: ", e);
            throw e;
        }
    }

    private void initializePermissions() {
        log.info("Initializing permissions...");

        String[] permissionNames = {
                "USER_READ", "USER_WRITE", "USER_DELETE",
                "QUESTION_READ", "QUESTION_WRITE", "QUESTION_DELETE",
                "SUBJECT_READ", "SUBJECT_WRITE", "SUBJECT_DELETE",
                "CHAPTER_READ", "CHAPTER_WRITE", "CHAPTER_DELETE"
        };

        for (String name : permissionNames) {
            if (!permissionRepository.existsByName(name)) {
                Permission permission = Permission.builder()
                        .name(name)
                        .value(true)
                        .build();
                permissionRepository.save(permission);
                log.debug("Created permission: {}", name);
            }
        }
    }

    private void initializeRoles() {
        log.info("Initializing roles...");

        // Create ADMIN role with all permissions
        if (!roleRepository.existsByName("ADMIN")) {
            Set<Permission> allPermissions = new HashSet<>(permissionRepository.findAll());
            Role adminRole = Role.builder()
                    .name("ADMIN")
                    .permissions(allPermissions)
                    .build();
            roleRepository.save(adminRole);
            log.debug("Created ADMIN role with {} permissions", allPermissions.size());
        }

        // Create TEACHER role with limited permissions
        if (!roleRepository.existsByName("TEACHER")) {
            Set<Permission> teacherPermissions = new HashSet<>();
            String[] teacherPermissionNames = {
                    "USER_READ", "QUESTION_READ", "QUESTION_WRITE", "QUESTION_DELETE",
                    "SUBJECT_READ", "SUBJECT_WRITE", "CHAPTER_READ", "CHAPTER_WRITE", "CHAPTER_DELETE"
            };

            for (String permName : teacherPermissionNames) {
                permissionRepository.findByName(permName).ifPresent(teacherPermissions::add);
            }

            Role teacherRole = Role.builder()
                    .name("TEACHER")
                    .permissions(teacherPermissions)
                    .build();
            roleRepository.save(teacherRole);
            log.debug("Created TEACHER role with {} permissions", teacherPermissions.size());
        }

        // Create STUDENT role with read-only permissions
        if (!roleRepository.existsByName("STUDENT")) {
            Set<Permission> studentPermissions = new HashSet<>();
            String[] studentPermissionNames = {
                    "USER_READ", "QUESTION_READ", "SUBJECT_READ", "CHAPTER_READ"
            };

            for (String permName : studentPermissionNames) {
                permissionRepository.findByName(permName).ifPresent(studentPermissions::add);
            }

            Role studentRole = Role.builder()
                    .name("STUDENT")
                    .permissions(studentPermissions)
                    .build();
            roleRepository.save(studentRole);
            log.debug("Created STUDENT role with {} permissions", studentPermissions.size());
        }
    }

    private void initializeUsers() {
        log.info("Initializing default users...");

        // Create Admin User
        if (!userRepository.existsByName("admin")) {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

            User admin = User.builder()
                    .name("admin")
                    .password(passwordEncoder.encode("password123"))
                    .roles(Set.of(adminRole))
                    .build();
            userRepository.save(admin);
            log.debug("Created admin user");
        }

        // Create Teacher User
        if (!userRepository.existsByName("teacher1")) {
            Role teacherRole = roleRepository.findByName("TEACHER")
                    .orElseThrow(() -> new RuntimeException("TEACHER role not found"));

            User teacher = User.builder()
                    .name("teacher1")
                    .password(passwordEncoder.encode("password123"))
                    .roles(Set.of(teacherRole))
                    .build();
            userRepository.save(teacher);
            log.debug("Created teacher1 user");
        }

        // Create Student User
        if (!userRepository.existsByName("student1")) {
            Role studentRole = roleRepository.findByName("STUDENT")
                    .orElseThrow(() -> new RuntimeException("STUDENT role not found"));

            User student = User.builder()
                    .name("student1")
                    .password(passwordEncoder.encode("password123"))
                    .roles(Set.of(studentRole))
                    .build();
            userRepository.save(student);
            log.debug("Created student1 user");
        }
    }
}