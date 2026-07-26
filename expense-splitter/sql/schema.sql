-- ===========================================
-- Smart Expense Splitter Database
-- ===========================================

CREATE DATABASE IF NOT EXISTS expense_splitter;
USE expense_splitter;

-- ===========================================
-- USERS
-- ===========================================

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===========================================
-- GROUPS
-- ===========================================

CREATE TABLE groups_table (
    group_id INT AUTO_INCREMENT PRIMARY KEY,
    group_name VARCHAR(100) NOT NULL,
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (created_by)
    REFERENCES users(user_id)
    ON DELETE CASCADE
);

-- ===========================================
-- GROUP MEMBERS
-- ===========================================

CREATE TABLE group_members (
    group_member_id INT AUTO_INCREMENT PRIMARY KEY,
    group_id INT NOT NULL,
    user_id INT NOT NULL,

    FOREIGN KEY (group_id)
    REFERENCES groups_table(group_id)
    ON DELETE CASCADE,

    FOREIGN KEY (user_id)
    REFERENCES users(user_id)
    ON DELETE CASCADE,

    UNIQUE(group_id,user_id)
);

-- ===========================================
-- EXPENSES
-- ===========================================

CREATE TABLE expenses (
    expense_id INT AUTO_INCREMENT PRIMARY KEY,
    group_id INT,
    paid_by INT NOT NULL,
    description VARCHAR(200),
    total_amount DECIMAL(10,2) NOT NULL,
    expense_date DATE DEFAULT (CURRENT_DATE),

    FOREIGN KEY (group_id)
    REFERENCES groups_table(group_id)
    ON DELETE CASCADE,

    FOREIGN KEY (paid_by)
    REFERENCES users(user_id)
);

-- ===========================================
-- EXPENSE SPLITS
-- ===========================================

CREATE TABLE expense_splits (
    split_id INT AUTO_INCREMENT PRIMARY KEY,
    expense_id INT NOT NULL,
    user_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,

    FOREIGN KEY (expense_id)
    REFERENCES expenses(expense_id)
    ON DELETE CASCADE,

    FOREIGN KEY (user_id)
    REFERENCES users(user_id)
);

-- ===========================================
-- SETTLEMENTS
-- ===========================================

CREATE TABLE settlements (
    settlement_id INT AUTO_INCREMENT PRIMARY KEY,
    payer_id INT NOT NULL,
    receiver_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    settlement_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (payer_id)
    REFERENCES users(user_id),

    FOREIGN KEY (receiver_id)
    REFERENCES users(user_id)
);

-- ===========================================
-- SAMPLE USERS
-- ===========================================

INSERT INTO users(name,email,phone) VALUES
('Prithi','prithi@gmail.com','9876543210'),
('Rahul','rahul@gmail.com','9876543211'),
('Anu','anu@gmail.com','9876543212'),
('Karthik','karthik@gmail.com','9876543213');

-- ===========================================
-- SAMPLE GROUP
-- ===========================================

INSERT INTO groups_table(group_name,created_by)
VALUES('Goa Trip',1);

-- ===========================================
-- GROUP MEMBERS
-- ===========================================

INSERT INTO group_members(group_id,user_id)
VALUES
(1,1),
(1,2),
(1,3),
(1,4);

-- ===========================================
-- SAMPLE EXPENSE
-- ===========================================

INSERT INTO expenses(group_id,paid_by,description,total_amount)
VALUES
(1,1,'Hotel Booking',4000);

-- ===========================================
-- EXPENSE SPLITS
-- ===========================================

INSERT INTO expense_splits(expense_id,user_id,amount)
VALUES
(1,1,1000),
(1,2,1000),
(1,3,1000),
(1,4,1000);

-- ===========================================
-- SAMPLE SETTLEMENT
-- ===========================================

INSERT INTO settlements(payer_id,receiver_id,amount)
VALUES
(2,1,1000);