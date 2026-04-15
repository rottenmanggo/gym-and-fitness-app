CREATE DATABASE IF NOT EXISTS gymbrut;
USE gymbrut;

CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL
);

INSERT INTO roles (id, role_name) VALUES
(1, 'ADMIN'),
(2, 'MEMBER')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE membership_packages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    package_name VARCHAR(100) NOT NULL,
    duration_days INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description TEXT
);

CREATE TABLE memberships (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    package_id INT NOT NULL,
    start_date DATE,
    end_date DATE,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_memberships_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_memberships_package FOREIGN KEY (package_id) REFERENCES membership_packages(id)
);

CREATE TABLE payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    membership_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date DATE,
    payment_method VARCHAR(50),
    status VARCHAR(50),
    proof_image_path TEXT,
    CONSTRAINT fk_payments_membership FOREIGN KEY (membership_id) REFERENCES memberships(id)
);

CREATE TABLE checkins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    checkin_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_checkins_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE workouts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    level VARCHAR(50),
    muscle_group VARCHAR(50),
    duration_minutes INT,
    calories_burn INT,
    video_url TEXT,
    image_path TEXT
);

CREATE TABLE workout_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    workout_id INT NOT NULL,
    log_date DATE,
    sets_count INT,
    reps_count INT,
    duration_minutes INT,
    weight_used DECIMAL(7,2),
    notes TEXT,
    CONSTRAINT fk_workout_logs_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_workout_logs_workout FOREIGN KEY (workout_id) REFERENCES workouts(id)
);

INSERT INTO users (role_id, name, email, password, phone) VALUES
(1, 'Marcus Thorne', 'admin@gymbrut.com', 'admin123', '081200000001'),
(2, 'Dominic Toretto', 'member@gymbrut.com', 'member123', '081200000002')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO membership_packages (package_name, duration_days, price, description) VALUES
('Elite Performance', 365, 1200.00, 'Premium annual membership'),
('Power Lifter Pro', 180, 650.00, 'Strength-focused package'),
('Standard Build', 30, 149.00, 'Monthly package');
