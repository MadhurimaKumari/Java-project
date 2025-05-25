import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TodoListApp {
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/todolist_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";
    
    // UI components
    private JFrame frame;
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JTextField descriptionField;
    private JFormattedTextField deadlineField;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new TodoListApp().initialize();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void initialize() {
        createDatabaseIfNotExists();
        initializeUI();
        loadTasks();
    }

    private void createDatabaseIfNotExists() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // Create database if not exists
            stmt.execute("CREATE DATABASE IF NOT EXISTS todolist_db");
            
            // Use the database
            stmt.execute("USE todolist_db");
            
            // Create tasks table if not exists
            stmt.execute("CREATE TABLE IF NOT EXISTS tasks (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "description VARCHAR(255) NOT NULL, " +
                    "is_complete BOOLEAN DEFAULT FALSE, " +
                    "deadline DATE, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database initialization failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void initializeUI() {
        frame = new JFrame("To-Do List Application");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input panel at the top
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Description label and field
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Task Description:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        descriptionField = new JTextField(30);
        inputPanel.add(descriptionField, gbc);

        // Deadline label and field
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Deadline (YYYY-MM-DD):"), gbc);

        gbc.gridx = 1;
        deadlineField = new JFormattedTextField(dateFormatter);
        deadlineField.setColumns(10);
        inputPanel.add(deadlineField, gbc);

        // Add button
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = new JButton("Add Task");
        addButton.addActionListener(this::addTask);
        inputPanel.add(addButton, gbc);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Task table in the center
        String[] columnNames = {"ID", "Description", "Deadline", "Status", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        taskTable = new JTable(tableModel);
        taskTable.setRowHeight(30);
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane(taskTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadTasks());
        buttonPanel.add(refreshButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        buttonPanel.add(exitButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private void loadTasks() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tasks ORDER BY deadline ASC")) {
            
            tableModel.setRowCount(0);
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                LocalDate deadline = rs.getDate("deadline").toLocalDate();
                boolean isComplete = rs.getBoolean("is_complete");
                
                Object[] row = new Object[5];
                row[0] = id;
                row[1] = description;
                row[2] = deadline.format(dateFormatter);
                row[3] = isComplete ? "Completed" : "Pending";

                // Create action buttons
                JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                
                JButton toggleButton = new JButton(isComplete ? "Mark Pending" : "Mark Complete");
                toggleButton.addActionListener(e -> toggleTaskStatus(id, !isComplete));
                
                JButton deleteButton = new JButton("Delete");
                deleteButton.addActionListener(e -> deleteTask(id));
                
                JButton updateButton = new JButton("Update Deadline");
                updateButton.addActionListener(e -> updateDeadline(id));
                
                actionPanel.add(toggleButton);
                actionPanel.add(deleteButton);
                actionPanel.add(updateButton);
                
                row[4] = actionPanel;
                tableModel.addRow(row);
            }
            
            // Set custom renderer for the action column
            taskTable.getColumnModel().getColumn(4).setCellRenderer(new TableCellComponentRenderer());
            taskTable.getColumnModel().getColumn(4).setCellEditor(new TableCellComponentEditor());
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading tasks: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTask(ActionEvent e) {
        String description = descriptionField.getText().trim();
        String deadlineText = deadlineField.getText().trim();

        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a task description",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LocalDate deadline = LocalDate.parse(deadlineText, dateFormatter);
            if (deadline.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(frame, "Deadline cannot be in the past",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO tasks (description, deadline) VALUES (?, ?)")) {
                
                stmt.setString(1, description);
                stmt.setDate(2, Date.valueOf(deadline));
                stmt.executeUpdate();
                
                descriptionField.setText("");
                deadlineField.setText("");
                loadTasks();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Invalid date format. Please use YYYY-MM-DD",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleTaskStatus(int taskId, boolean newStatus) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE tasks SET is_complete = ? WHERE id = ?")) {
            
            stmt.setBoolean(1, newStatus);
            stmt.setInt(2, taskId);
            stmt.executeUpdate();
            loadTasks();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error updating task status: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTask(int taskId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM tasks WHERE id = ?")) {
            
            stmt.setInt(1, taskId);
            stmt.executeUpdate();
            loadTasks();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error deleting task: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDeadline(int taskId) {
        String newDeadlineStr = JOptionPane.showInputDialog(frame, 
                "Enter new deadline (YYYY-MM-DD):", 
                "Update Deadline", 
                JOptionPane.PLAIN_MESSAGE);
        
        if (newDeadlineStr != null && !newDeadlineStr.trim().isEmpty()) {
            try {
                LocalDate newDeadline = LocalDate.parse(newDeadlineStr.trim(), dateFormatter);
                if (newDeadline.isBefore(LocalDate.now())) {
                    JOptionPane.showMessageDialog(frame, "Deadline cannot be in the past",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try (Connection conn = getConnection();
                     PreparedStatement stmt = conn.prepareStatement(
                             "UPDATE tasks SET deadline = ? WHERE id = ?")) {
                    
                    stmt.setDate(1, Date.valueOf(newDeadline));
                    stmt.setInt(2, taskId);
                    stmt.executeUpdate();
                    loadTasks();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Invalid date format. Please use YYYY-MM-DD",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Helper classes for table cell components
    private static class TableCellComponentRenderer implements javax.swing.table.TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            return (Component) value;
        }
    }

    private static class TableCellComponentEditor extends javax.swing.DefaultCellEditor {
        public TableCellComponentEditor() {
            super(new JTextField());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, 
                boolean isSelected, int row, int column) {
            return (Component) value;
        }
    }
}