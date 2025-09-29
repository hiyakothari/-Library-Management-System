
# Library Management System

A desktop application for managing library operations including book inventory, user management, and book lending/returning processes.

## Features

- **Book Management**: Add, update, delete, and search books
- **User Management**: Manage library members with their contact information
- **Checkout System**: Issue books to users with automatic availability tracking
- **Return System**: Process book returns and update inventory in real-time
- **Borrowed Books Tracking**: View all currently borrowed books with due dates
- **Search Functionality**: Quick search for books and users
- **Real-time Inventory Updates**: Automatic tracking of available vs. total quantities

## Technologies Used

- **Java Swing**: For the graphical user interface
- **MySQL**: Database management system
- **JDBC**: Java Database Connectivity for database operations
- **Java 8+**: Core programming language


## Database Schema

### Books Table
- `book_id` (INT, Primary Key, Auto Increment)
- `title` (VARCHAR)
- `author` (VARCHAR)
- `isbn` (VARCHAR, Unique)
- `total_quantity` (INT)
- `available_quantity` (INT)

### Users Table
- `user_id` (INT, Primary Key, Auto Increment)
- `name` (VARCHAR)
- `email` (VARCHAR, Unique)
- `phone` (VARCHAR)

### Borrowed Books Table
- `borrow_id` (INT, Primary Key, Auto Increment)
- `book_id` (INT, Foreign Key)
- `user_id` (INT, Foreign Key)
- `borrow_date` (DATE)
- `due_date` (DATE)
- `return_date` (DATE)
- `status` (VARCHAR)

## Usage

### Managing Books
1. Navigate to the **Books** tab
2. Fill in book details (Title, Author, ISBN, Quantity)
3. Click **Add Book** to add a new book
4. Select a book from the table to update or delete it
5. Use the search bar to find specific books

### Managing Users
1. Navigate to the **Users** tab
2. Enter user information (Name, Email, Phone)
3. Click **Add User** to register a new user
4. Select a user from the table to update or delete their information

### Checking Out Books
1. Navigate to the **Checkout** tab
2. Enter the Book ID and User ID
3. Click **Checkout Book**
4. The system automatically sets a due date (14 days from checkout)
5. Available quantity is updated automatically

### Returning Books
1. Navigate to the **Return** tab
2. Enter the Book ID and User ID
3. Click **Return Book**
4. The system updates the inventory and marks the book as returned

### Viewing Borrowed Books
1. Navigate to the **Borrowed Books** tab
2. View all currently borrowed books with user details and due dates
3. Click **Refresh** to update the list


