# 📝 To-Do List Application (Java Swing + MySQL)

A fully functional **GUI-based To-Do List Application** built using **Java Swing** and **MySQL**, featuring task creation, deadline tracking, status updates, deletion, and a **real-time search filter** for improved usability.

---

## 🔧 Features

| Feature                     | Description |
|----------------------------|-------------|
| ✅ **Add Task**            | Enter task description and deadline. |
| 📅 **Deadline Validation** | Prevents tasks from having past deadlines. |
| 🔁 **Mark Complete/Pending** | Toggle status of any task. |
| 🗑️ **Delete Task**         | Remove tasks individually. |
| ✏️ **Update Deadline**     | Change task deadline from the table. |
| 🔍 **Real-time Search Filter** | Instantly search/filter tasks as you type. |
| 💾 **Persistent Storage**  | Tasks are stored in a MySQL database. |
| 🛡️ **Robust Error Handling** | Catches SQL and input errors gracefully. |
| 🧼 **Clean UI**            | Responsive and intuitive Java Swing GUI. |

---

## 🗂️ Project Structure

```plaintext
TodoListApp.java           # Main class with GUI and business logic
└── initialize()           # Sets up DB and GUI
└── createDatabaseIfNotExists() # Creates DB and tasks table if not present
└── initializeUI()         # Builds Swing interface
└── loadTasks()            # Loads task data from DB into JTable
└── addTask()              # Adds new task with validation
└── toggleTaskStatus()     # Marks task as complete/incomplete
└── deleteTask()           # Deletes task by ID
└── updateDeadline()       # Changes deadline using input dialog
└── TableCellRenderer/Editor # Handles buttons in JTable cells



##  Project Root

project-root/

├── src/

│ └── TodoListApp.java

├── README.md

└── database/

└── Automatically created on first run



## 🔍 Function Explanation

| Function Name                       | Description                                                                 |
|------------------------------------|-----------------------------------------------------------------------------|
| `main(String[] args)`              | Entry point of the app. Sets look & feel, calls `initialize()`             |
| `initialize()`                     | Main initializer: creates DB if needed, sets up UI, and loads tasks        |
| `createDatabaseIfNotExists()`      | Connects to MySQL and creates the `todolist_db` database and `tasks` table |
| `initializeUI()`                   | Builds and lays out all GUI components (frame, buttons, fields, table, etc.) |
| `getConnection()`                  | Returns a connection object to the MySQL database                          |
| `loadTasks()`                      | Reads all tasks from the DB and populates the JTable with them             |
| `addTask(ActionEvent e)`          | Adds a new task to the DB using input values (with deadline validation)    |
| `toggleTaskStatus(int taskId, boolean newStatus)` | Marks task as complete/pending                                  |
| `deleteTask(int taskId)`          | Deletes task by ID                                                         |
| `updateDeadline(int taskId)`      | Prompts user to update a task's deadline, then updates it in the DB        |
| `TableCellComponentRenderer`      | Allows GUI components (like buttons) inside a JTable cell                  |
| `TableCellComponentEditor`        | Enables interaction with GUI components inside a JTable cell               |



🧠 Functionality in Flow (Simplified)
App Launch

main() → initialize() → createDatabaseIfNotExists() + initializeUI() + loadTasks()

User Adds a Task

Inputs description and deadline → Clicks "Add Task"

addTask() is triggered → Validates data → Inserts into DB → Refreshes table

User Interacts with Task Row

Clicks "Mark Complete" / "Mark Pending" → toggleTaskStatus() updates DB

Clicks "Delete" → deleteTask() removes from DB

Clicks "Update Deadline" → updateDeadline() updates due date

User Filters Tasks

Types into search bar → real-time filtering updates the visible tasks in the table



🧠 Core Technologies

Java Swing – GUI framework

MySQL – Backend database

JDBC – Java Database Connectivity

Maven (Optional) – Can be used for dependency management (not required here)



🚀 How to Run


🛠️ Prerequisites

Java JDK 8 or above

MySQL Server (with user root and password password)

MySQL JDBC Driver


💻 Steps

Create Database Automatically

The program creates todolist_db and tasks table on first run.

Compile & Run

bash
javac TodoListApp.java
java TodoListApp
Connects to MySQL

Ensure your MySQL service is running.

Change DB credentials in code if needed:

java
private static final String DB_URL = "jdbc:mysql://localhost:3306/todolist_db";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "password";


🧪 Sample Table Schema

sql
CREATE TABLE tasks (

  id INT AUTO_INCREMENT PRIMARY KEY,
  description VARCHAR(255) NOT NULL,
  is_complete BOOLEAN DEFAULT FALSE,
  deadline DATE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);




🪄 Innovative Feature

🔍 Real-Time Task Filtering:

A live search bar filters tasks instantly while typing. This improves usability in larger task lists and helps locate tasks faster.


🧾 Marking Rubric Mapping

Criteria	How it’s Implemented

✅ Core Features	Add, update, delete, mark complete/pending

🛑 Error Handling & Robustness	Input checks, SQL exception handling

🔁 Component Integration	GUI ↔ DB ↔ Logic

⚡ Event Handling	ActionListeners for all buttons

🔒 Data Validation	Input check, date format, future deadline only

🧼 Code Quality & Innovation	Modular code + Real-time search filter

📚 Project Documentation	This README + inline comments


📬 Contact

For support or questions:

📧 kumarimadhurima785@gmail.com

🌐 https://github.com/MadhurimaKumari/Java-project

Made with ❤️ by Madhurima Kumari
