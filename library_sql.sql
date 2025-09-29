-- Create the database
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- Books table
CREATE TABLE IF NOT EXISTS books (
    book_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    total_quantity INT DEFAULT 0,
    available_quantity INT DEFAULT 0
);

-- Users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(15)
);

-- Borrowed books table
CREATE TABLE IF NOT EXISTS borrowed_books (
    borrow_id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT,
    user_id INT,
    borrow_date DATE,
    due_date DATE,
    return_date DATE,
    status VARCHAR(20) DEFAULT 'BORROWED',
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Insert sample books
INSERT INTO books (title, author, isbn, total_quantity, available_quantity) VALUES
('The Great Gatsby', 'F. Scott Fitzgerald', '978-0743273565', 5, 5),
('To Kill a Mockingbird', 'Harper Lee', '978-0061120084', 3, 3),
('1984', 'George Orwell', '978-0451524935', 4, 4),
('Pride and Prejudice', 'Jane Austen', '978-0141439518', 3, 3),
('The Catcher in the Rye', 'J.D. Salinger', '978-0316769174', 2, 2);

-- Insert sample users
INSERT INTO users (name, email, phone) VALUES
('John Doe', 'john.doe@email.com', '555-0101'),
('Jane Smith', 'jane.smith@email.com', '555-0102'),
('Bob Johnson', 'bob.johnson@email.com', '555-0103'),
('Alice Williams', 'alice.williams@email.com', '555-0104');