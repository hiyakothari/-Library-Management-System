
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

## Prerequisites

Before running this application, make sure you have:

- Java Development Kit (JDK) 8 or higher
- MySQL Server (5.7 or higher)
- MySQL Connector/J (JDBC Driver)


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

## Project Structure

```
library-management-system/
├── src/
│   └── LibraryManagementSystem.java
├── lib/
│   └── mysql-connector-java-8.x.x.jar (not included in repo)
├── database_setup.sql
├── .gitignore
└── README.md
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Future Enhancements

- [ ] Fine calculation for overdue books
- [ ] Book reservation system
- [ ] Export reports to PDF/Excel
- [ ] User authentication and role-based access
- [ ] Email notifications for due dates
- [ ] Book category/genre classification
- [ ] Advanced search filters
- [ ] Dashboard with statistics

## License

This project is open source and available under the [MIT License](LICENSE).

## Contact

Your Name - your.email@example.com

Project Link: [https://github.com/yourusername/library-management-system](https://github.com/yourusername/library-management-system)

## Acknowledgments

- Java Swing documentation
- MySQL documentation
- JDBC tutorials
