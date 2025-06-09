import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TodoListApp {
    // Database configuration constants
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
        // Set UI look and feel and launch the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new TodoListApp().initialize();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Initializes the application:
     *  - Creates the database and table if not already existing
     *  - Sets up the user interface
     *  - Loads tasks from the database into the table
     */
    private void initialize() {
        createDatabaseIfNotExists();
        initializeUI();
        loadTasks();
    }

    /**
     * Creates the database and tasks table if they don't exist.
     * Handles any SQL exceptions by showing an error and exiting.
     */
    private void createDatabaseIfNotExists() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            // Create database if it does not exist
            stmt.execute("CREATE DATABASE IF NOT EXISTS todolist_db");
            // Switch to the database
            stmt.execute("USE todolist_db");
            // Create tasks table if it does not exist
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

    /**
     * Sets up the GUI, including input fields, buttons, and the task table.
     * Uses BorderLayout and GridBagLayout for flexible and neat layout.
     */
    private void initializeUI() {
        frame = new JFrame("To-Do List Application");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Main panel with padding and BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input panel for task description and deadline
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label for task description
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Task Description:"), gbc);

        // Text field for task description input
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        descriptionField = new JTextField(30);
        inputPanel.add(descriptionField, gbc);

        // Label for deadline input
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Deadline (YYYY-MM-DD):"), gbc);

        // Formatted text field for deadline input
        gbc.gridx = 1;
        deadlineField = new JFormattedTextField(dateFormatter);
        deadlineField.setColumns(10);
        inputPanel.add(deadlineField, gbc);

        // Add Task button triggers addTask() method
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = new JButton("Add Task");
        addButton.addActionListener(this::addTask);
        inputPanel.add(addButton, gbc);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Table to display tasks with columns for ID, Description, Deadline, Status, and Actions
        String[] columnNames = {"ID", "Description", "Deadline", "Status", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            // Make table cells non-editable directly by users
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        taskTable = new JTable(tableModel);
        taskTable.setRowHeight(30);
        // Set preferred widths for better UI
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane(taskTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with Refresh and Exit buttons
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

    /**
     * Utility method to get a connection to the MySQL database.
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Loads tasks from the database and populates the JTable.
     * Each row has action buttons to update, delete, or toggle status.
     */
    private void loadTasks() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tasks ORDER BY deadline ASC")) {

            tableModel.setRowCount(0);  // Clear existing rows

            while (rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                Date deadlineDate = rs.getDate("deadline");
                String deadlineStr = deadlineDate != null ? deadlineDate.toLocalDate().format(dateFormatter) : "No deadline";
                boolean isComplete = rs.getBoolean("is_complete");

                // Prepare row data
                Object[] row = new Object[5];
                row[0] = id;
                row[1] = description;
                row[2] = deadlineStr;
                row[3] = isComplete ? "Completed" : "Pending";

                // Panel with action buttons: Toggle status, Delete, Update Deadline
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

            // Set custom renderer and editor for the Actions column to display buttons properly
            taskTable.getColumnModel().getColumn(4).setCellRenderer(new TableCellComponentRenderer());
            taskTable.getColumnModel().getColumn(4).setCellEditor(new TableCellComponentEditor());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading tasks: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Adds a new task to the database using the description and deadline input fields.
     * Includes validation to ensure description is not empty and deadline is a valid future date.
     */
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

                // Clear input fields and reload table
                descriptionField.setText("");
                deadlineField.setText("");
                loadTasks();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Invalid date format. Please use YYYY-MM-DD",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Toggles the completion status of a task.
     */
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

    /**
     * Deletes a task by its ID.
     */
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

    /**
     * Prompts the user to enter a new deadline for a task, validates it,
     * and updates the database.
     */
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

    // Custom renderer to display JPanel (with buttons) inside JTable cells
    private static class TableCellComponentRenderer implements javax.swing.table.TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return (Component) value;
        }
    }

    // Custom editor to allow interaction with components inside JTable cells (like buttons)
    private static class TableCellComponentEditor extends javax.swing.DefaultCellEditor {
        public TableCellComponentEditor() {
            super(new JTextField()); // Required by superclass but unused
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            return (Component) value;
        }
    }
}
