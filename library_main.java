import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LibraryManagementSystem extends JFrame {
    private Connection conn;
    private JTabbedPane tabbedPane;
    
    // Book Management Components
    private JTable bookTable;
    private DefaultTableModel bookTableModel;
    private JTextField txtBookId, txtTitle, txtAuthor, txtISBN, txtQuantity;
    private JTextField searchBookField;
    
    // User Management Components
    private JTable userTable;
    private DefaultTableModel userTableModel;
    private JTextField txtUserId, txtUserName, txtEmail, txtPhone;
    private JTextField searchUserField;
    
    // Checkout Components
    private JTextField txtCheckoutBookId, txtCheckoutUserId;
    private JTable checkoutTable;
    private DefaultTableModel checkoutTableModel;
    
    // Return Components
    private JTextField txtReturnBookId, txtReturnUserId;
    
    public LibraryManagementSystem() {
        setTitle("Library Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize database connection
        initDatabase();
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Books", createBooksPanel());
        tabbedPane.addTab("Users", createUsersPanel());
        tabbedPane.addTab("Checkout", createCheckoutPanel());
        tabbedPane.addTab("Return", createReturnPanel());
        tabbedPane.addTab("Borrowed Books", createBorrowedBooksPanel());
        
        add(tabbedPane);
        
        // Load initial data
        loadBooks();
        loadUsers();
        loadBorrowedBooks();
    }
    
    private void initDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/library_db", 
                "root", 
                "password"
            );
            createTables();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Database connection failed: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void createTables() {
        try {
            Statement stmt = conn.createStatement();
            
            // Books table
            stmt.execute("CREATE TABLE IF NOT EXISTS books (" +
                "book_id INT PRIMARY KEY AUTO_INCREMENT," +
                "title VARCHAR(200) NOT NULL," +
                "author VARCHAR(100) NOT NULL," +
                "isbn VARCHAR(20) UNIQUE," +
                "total_quantity INT DEFAULT 0," +
                "available_quantity INT DEFAULT 0)");
            
            // Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "user_id INT PRIMARY KEY AUTO_INCREMENT," +
                "name VARCHAR(100) NOT NULL," +
                "email VARCHAR(100) UNIQUE," +
                "phone VARCHAR(15))");
            
            // Borrowed books table
            stmt.execute("CREATE TABLE IF NOT EXISTS borrowed_books (" +
                "borrow_id INT PRIMARY KEY AUTO_INCREMENT," +
                "book_id INT," +
                "user_id INT," +
                "borrow_date DATE," +
                "due_date DATE," +
                "return_date DATE," +
                "status VARCHAR(20) DEFAULT 'BORROWED'," +
                "FOREIGN KEY (book_id) REFERENCES books(book_id)," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id))");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel for search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchBookField = new JTextField(20);
        searchPanel.add(searchBookField);
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> searchBooks());
        searchPanel.add(btnSearch);
        
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadBooks());
        searchPanel.add(btnRefresh);
        
        // Table
        String[] columns = {"ID", "Title", "Author", "ISBN", "Total Qty", "Available Qty"};
        bookTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(bookTableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Book Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        txtBookId = new JTextField(10);
        txtTitle = new JTextField(20);
        txtAuthor = new JTextField(20);
        txtISBN = new JTextField(15);
        txtQuantity = new JTextField(10);
        
        txtBookId.setEditable(false);
        
        addFormField(formPanel, gbc, 0, "Book ID:", txtBookId);
        addFormField(formPanel, gbc, 1, "Title:", txtTitle);
        addFormField(formPanel, gbc, 2, "Author:", txtAuthor);
        addFormField(formPanel, gbc, 3, "ISBN:", txtISBN);
        addFormField(formPanel, gbc, 4, "Quantity:", txtQuantity);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Add Book");
        JButton btnUpdate = new JButton("Update Book");
        JButton btnDelete = new JButton("Delete Book");
        JButton btnClear = new JButton("Clear");
        
        btnAdd.addActionListener(e -> addBook());
        btnUpdate.addActionListener(e -> updateBook());
        btnDelete.addActionListener(e -> deleteBook());
        btnClear.addActionListener(e -> clearBookForm());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        
        formPanel.add(buttonPanel, gbc);
        
        // Table click listener
        bookTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = bookTable.getSelectedRow();
                if (row != -1) {
                    txtBookId.setText(bookTable.getValueAt(row, 0).toString());
                    txtTitle.setText(bookTable.getValueAt(row, 1).toString());
                    txtAuthor.setText(bookTable.getValueAt(row, 2).toString());
                    txtISBN.setText(bookTable.getValueAt(row, 3).toString());
                    txtQuantity.setText(bookTable.getValueAt(row, 4).toString());
                }
            }
        });
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchUserField = new JTextField(20);
        searchPanel.add(searchUserField);
        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> searchUsers());
        searchPanel.add(btnSearch);
        
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadUsers());
        searchPanel.add(btnRefresh);
        
        // Table
        String[] columns = {"ID", "Name", "Email", "Phone"};
        userTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userTableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("User Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        txtUserId = new JTextField(10);
        txtUserName = new JTextField(20);
        txtEmail = new JTextField(20);
        txtPhone = new JTextField(15);
        
        txtUserId.setEditable(false);
        
        addFormField(formPanel, gbc, 0, "User ID:", txtUserId);
        addFormField(formPanel, gbc, 1, "Name:", txtUserName);
        addFormField(formPanel, gbc, 2, "Email:", txtEmail);
        addFormField(formPanel, gbc, 3, "Phone:", txtPhone);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Add User");
        JButton btnUpdate = new JButton("Update User");
        JButton btnDelete = new JButton("Delete User");
        JButton btnClear = new JButton("Clear");
        
        btnAdd.addActionListener(e -> addUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnClear.addActionListener(e -> clearUserForm());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        
        formPanel.add(buttonPanel, gbc);
        
        // Table click listener
        userTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = userTable.getSelectedRow();
                if (row != -1) {
                    txtUserId.setText(userTable.getValueAt(row, 0).toString());
                    txtUserName.setText(userTable.getValueAt(row, 1).toString());
                    txtEmail.setText(userTable.getValueAt(row, 2).toString());
                    txtPhone.setText(userTable.getValueAt(row, 3).toString());
                }
            }
        });
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createCheckoutPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel title = new JLabel("Checkout Book");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);
        
        gbc.gridwidth = 1;
        txtCheckoutBookId = new JTextField(15);
        txtCheckoutUserId = new JTextField(15);
        
        addFormField(panel, gbc, 1, "Book ID:", txtCheckoutBookId);
        addFormField(panel, gbc, 2, "User ID:", txtCheckoutUserId);
        
        JButton btnCheckout = new JButton("Checkout Book");
        btnCheckout.addActionListener(e -> checkoutBook());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(btnCheckout, gbc);
        
        return panel;
    }
    
    private JPanel createReturnPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel title = new JLabel("Return Book");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);
        
        gbc.gridwidth = 1;
        txtReturnBookId = new JTextField(15);
        txtReturnUserId = new JTextField(15);
        
        addFormField(panel, gbc, 1, "Book ID:", txtReturnBookId);
        addFormField(panel, gbc, 2, "User ID:", txtReturnUserId);
        
        JButton btnReturn = new JButton("Return Book");
        btnReturn.addActionListener(e -> returnBook());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(btnReturn, gbc);
        
        return panel;
    }
    
    private JPanel createBorrowedBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columns = {"Borrow ID", "Book ID", "Book Title", "User ID", "User Name", 
                           "Borrow Date", "Due Date", "Status"};
        checkoutTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        checkoutTable = new JTable(checkoutTableModel);
        JScrollPane scrollPane = new JScrollPane(checkoutTable);
        
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadBorrowedBooks());
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(btnRefresh);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, 
                             String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        panel.add(field, gbc);
    }
    
    private void loadBooks() {
        bookTableModel.setRowCount(0);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM books");
            while (rs.next()) {
                bookTableModel.addRow(new Object[]{
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getInt("total_quantity"),
                    rs.getInt("available_quantity")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage());
        }
    }
    
    private void loadUsers() {
        userTableModel.setRowCount(0);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
            while (rs.next()) {
                userTableModel.addRow(new Object[]{
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }
    }
    
    private void loadBorrowedBooks() {
        checkoutTableModel.setRowCount(0);
        try {
            String query = "SELECT bb.borrow_id, bb.book_id, b.title, bb.user_id, u.name, " +
                          "bb.borrow_date, bb.due_date, bb.status " +
                          "FROM borrowed_books bb " +
                          "JOIN books b ON bb.book_id = b.book_id " +
                          "JOIN users u ON bb.user_id = u.user_id " +
                          "WHERE bb.status = 'BORROWED'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                checkoutTableModel.addRow(new Object[]{
                    rs.getInt("borrow_id"),
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getDate("borrow_date"),
                    rs.getDate("due_date"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading borrowed books: " + e.getMessage());
        }
    }
    
    private void addBook() {
        try {
            String sql = "INSERT INTO books (title, author, isbn, total_quantity, available_quantity) " +
                        "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, txtTitle.getText());
            pstmt.setString(2, txtAuthor.getText());
            pstmt.setString(3, txtISBN.getText());
            int qty = Integer.parseInt(txtQuantity.getText());
            pstmt.setInt(4, qty);
            pstmt.setInt(5, qty);
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Book added successfully!");
            clearBookForm();
            loadBooks();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding book: " + e.getMessage());
        }
    }
    
    private void updateBook() {
        try {
            String sql = "UPDATE books SET title=?, author=?, isbn=?, total_quantity=? " +
                        "WHERE book_id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, txtTitle.getText());
            pstmt.setString(2, txtAuthor.getText());
            pstmt.setString(3, txtISBN.getText());
            pstmt.setInt(4, Integer.parseInt(txtQuantity.getText()));
            pstmt.setInt(5, Integer.parseInt(txtBookId.getText()));
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Book updated successfully!");
            clearBookForm();
            loadBooks();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating book: " + e.getMessage());
        }
    }
    
    private void deleteBook() {
        try {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this book?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String sql = "DELETE FROM books WHERE book_id=?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, Integer.parseInt(txtBookId.getText()));
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Book deleted successfully!");
                clearBookForm();
                loadBooks();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting book: " + e.getMessage());
        }
    }
    
    private void addUser() {
        try {
            String sql = "INSERT INTO users (name, email, phone) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, txtUserName.getText());
            pstmt.setString(2, txtEmail.getText());
            pstmt.setString(3, txtPhone.getText());
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "User added successfully!");
            clearUserForm();
            loadUsers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding user: " + e.getMessage());
        }
    }
    
    private void updateUser() {
        try {
            String sql = "UPDATE users SET name=?, email=?, phone=? WHERE user_id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, txtUserName.getText());
            pstmt.setString(2, txtEmail.getText());
            pstmt.setString(3, txtPhone.getText());
            pstmt.setInt(4, Integer.parseInt(txtUserId.getText()));
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "User updated successfully!");
            clearUserForm();
            loadUsers();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating user: " + e.getMessage());
        }
    }
    
    private void deleteUser() {
        try {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this user?", 
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String sql = "DELETE FROM users WHERE user_id=?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, Integer.parseInt(txtUserId.getText()));
                pstmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
                clearUserForm();
                loadUsers();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage());
        }
    }
    
    private void checkoutBook() {
        try {
            int bookId = Integer.parseInt(txtCheckoutBookId.getText());
            int userId = Integer.parseInt(txtCheckoutUserId.getText());
            
            // Check availability
            String checkSql = "SELECT available_quantity FROM books WHERE book_id=?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                int available = rs.getInt("available_quantity");
                if (available > 0) {
                    // Insert borrow record
                    LocalDate today = LocalDate.now();
                    LocalDate dueDate = today.plusDays(14);
                    
                    String borrowSql = "INSERT INTO borrowed_books (book_id, user_id, borrow_date, due_date, status) " +
                                      "VALUES (?, ?, ?, ?, 'BORROWED')";
                    PreparedStatement borrowStmt = conn.prepareStatement(borrowSql);
                    borrowStmt.setInt(1, bookId);
                    borrowStmt.setInt(2, userId);
                    borrowStmt.setDate(3, Date.valueOf(today));
                    borrowStmt.setDate(4, Date.valueOf(dueDate));
                    borrowStmt.executeUpdate();
                    
                    // Update available quantity
                    String updateSql = "UPDATE books SET available_quantity = available_quantity - 1 WHERE book_id=?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                    updateStmt.setInt(1, bookId);
                    updateStmt.executeUpdate();
                    
                    JOptionPane.showMessageDialog(this, 
                        "Book checked out successfully!\nDue date: " + dueDate.format(DateTimeFormatter.ISO_DATE));
                    txtCheckoutBookId.setText("");
                    txtCheckoutUserId.setText("");
                    loadBooks();
                    loadBorrowedBooks();
                } else {
                    JOptionPane.showMessageDialog(this, "Book is not available!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Book not found!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error checking out book: " + e.getMessage());
        }
    }
    
    private void returnBook() {
        try {
            int bookId = Integer.parseInt(txtReturnBookId.getText());
            int userId = Integer.parseInt(txtReturnUserId.getText());
            
            // Find active borrow record
            String findSql = "SELECT borrow_id FROM borrowed_books " +
                           "WHERE book_id=? AND user_id=? AND status='BORROWED'";
            PreparedStatement findStmt = conn.prepareStatement(findSql);
            findStmt.setInt(1, bookId);
            findStmt.setInt(2, userId);
            ResultSet rs = findStmt.executeQuery();
            
            if (rs.next()) {
                LocalDate today = LocalDate.now();
                
                // Update borrow record
                String updateBorrowSql = "UPDATE borrowed_books SET return_date=?, status='RETURNED' " +
                                        "WHERE borrow_id=?";
                PreparedStatement updateBorrowStmt = conn.prepareStatement(updateBorrowSql);
                updateBorrowStmt.setDate(1, Date.valueOf(today));
                updateBorrowStmt.setInt(2, rs.getInt("borrow_id"));
                updateBorrowStmt.executeUpdate();
                
                // Update available quantity
                String updateBookSql = "UPDATE books SET available_quantity = available_quantity + 1 WHERE book_id=?";
                PreparedStatement updateBookStmt = conn.prepareStatement(updateBookSql);
                updateBookStmt.setInt(1, bookId);
                updateBookStmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Book returned successfully!");
                txtReturnBookId.setText("");
                txtReturnUserId.setText("");
                loadBooks();
                loadBorrowedBooks();
            } else {
                JOptionPane.showMessageDialog(this, "No active borrow record found!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error returning book: " + e.getMessage());
        }
    }
    
    private void searchBooks() {
        String search = searchBookField.getText().trim();
        if (search.isEmpty()) {
            loadBooks();
            return;
        }
        
        bookTableModel.setRowCount(0);
        try {
            String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            String searchPattern = "%" + search + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bookTableModel.addRow(new Object[]{
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getInt("total_quantity"),
                    rs.getInt("available_quantity")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching books: " + e.getMessage());
        }
    }
    
    private void searchUsers() {
        String search = searchUserField.getText().trim();
        if (search.isEmpty()) {
            loadUsers();
            return;
        }
        
        userTableModel.setRowCount(0);
        try {
            String sql = "SELECT * FROM users WHERE name LIKE ? OR email LIKE ? OR phone LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            String searchPattern = "%" + search + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                userTableModel.addRow(new Object[]{
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching users: " + e.getMessage());
        }
    }
    
    private void clearBookForm() {
        txtBookId.setText("");
        txtTitle.setText("");
        txtAuthor.setText("");
        txtISBN.setText("");
        txtQuantity.setText("");
        bookTable.clearSelection();
    }
    
    private void clearUserForm() {
        txtUserId.setText("");
        txtUserName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        userTable.clearSelection();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibraryManagementSystem frame = new LibraryManagementSystem();
            frame.setVisible(true);
        });
    }
}