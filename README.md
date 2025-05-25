# Java-project
A Java Swing-based task management application that stores tasks in a MySQL database, featuring:

->Add, edit, and delete tasks

->Mark tasks as complete/incomplete

->Set and update deadlines

->Persistent storage with MySQL




🛠️ Prerequisites

System Requirements

Java JDK 8 or later

MySQL Server 5.7 or later

MySQL Connector/J (included)




->Software Setup

->Install Java:

Download from Oracle JDK

Verify installation: java -version



->Install MySQL:

Download from MySQL Community Server

Set root password during installation



🚀 Installation & Setup

1. Database Configuration

##sql
CREATE DATABASE todolist_db;
USE todolist_db;

CREATE TABLE tasks (

    id INT AUTO_INCREMENT PRIMARY KEY,
    
    description VARCHAR(255) NOT NULL,
    
    is_complete BOOLEAN DEFAULT FALSE,
    
    deadline DATE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    
);



2. Application Setup

▶️ Running the Application
Windows

->cmd

->java -cp .;lib/mysql-connector-java-8.0.xx.jar TodoListApp

->Linux/Mac

bash

->java -cp .:lib/mysql-connector-java-8.0.xx.jar TodoListApp



🖥️ Application Walkthrough

->Main Interface:

*Task list displayed in table format

*Input fields for new tasks

*Action buttons for task management

*Adding a Task:

*Enter description

*Set deadline (YYYY-MM-DD)

*Click "Add Task"



Managing Tasks:

✅ Toggle completion status

📅 Update deadlines

❌ Delete tasks

🔄 Refresh task list




🔧 Troubleshooting

Issue	-  Solution

Database connection failed  -  Verify MySQL service is running and credentials are correct

ClassNotFoundException	-  Ensure MySQL Connector/J is in lib folder

Date format errors	-  Use YYYY-MM-DD format only

Table not found	-  Run the database setup script


📂 Project Structure

todo-list-app/

├── src/

│   └── TodoListApp.java       # Main application file

├── lib/

│   └── mysql-connector-java-8.0.xx.jar  # Database driver

├── database/

│   └── setup.sql              # Database schema

└── README.md




📬 Contact

For support or questions:

📧 kumarimadhurima785@gmail.com

🌐 https://github.com/MadhurimaKumari/Java-project



Made with ❤️ by Madhurima Kumari

