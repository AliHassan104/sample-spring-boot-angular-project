-- Insert Permissions
INSERT IGNORE INTO permissions (id, name, value) VALUES
(1, 'USER_READ', true),
(2, 'USER_WRITE', true),
(3, 'USER_DELETE', true),
(4, 'QUESTION_READ', true),
(5, 'QUESTION_WRITE', true),
(6, 'QUESTION_DELETE', true);

-- Insert Roles
INSERT IGNORE INTO roles (id, name) VALUES
(1, 'ADMIN'),
(2, 'TEACHER'),
(3, 'STUDENT');

-- Insert Role-Permission mappings
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6),
(2, 1), (2, 4), (2, 5), (2, 6),
(3, 1), (3, 4);

-- Insert Users (password: password123)
INSERT IGNORE INTO users (id, name, password) VALUES
(1, 'admin', '$2a$12$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a'),
(2, 'teacher1', '$2a$12$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a'),
(3, 'student1', '$2a$12$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a');

-- Insert User-Role mappings
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 3);