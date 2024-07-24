# Password Manager

A console-based password management system where users can securely manage their passwords. This project leverages Java, PostgreSQL, Data Structures and Algorithms (DSA), AES encryption, and an email API for a robust and secure password management solution.

## Features

- User Sign-Up and Login: Secure sign-up with email verification and login system.
- Password Management: Add, update, delete, and search passwords.
- AES Encryption: All passwords are stored securely using AES encryption.
- Email Verification: Email verification during user registration.
- Database: PostgreSQL database for storing user information and passwords.
- Based Interface: Simple and user-friendly console interface.

## Technologies Used

- Java: Backend development.
- PostgreSQL: Database management system.
- DSA: Efficient data handling and storage.
- AES Encryption: Secure password storage.
- Email API: Email verification service.

## Getting Started

### Prerequisites

- Java 11 or higher
- PostgreSQL
- Email service API (e.g., SendGrid, Mailgun)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/AnkushGitRepo/Password-Manager.git
   cd Password-Manager
   ```
   

2. Set up PostgreSQL:
   - Create a database and user for the project.
   - Update the database configuration in the `DatabaseHandler` class.

3. Configure Email API:
   - Update the email service configuration with your API credentials.

4. Compile and Run:
   ```bash
   javac -d bin src/*.java
   java -cp bin Main
   ```

### Usage

- Sign-Up: Register a new user account with email verification.
- Login: Access your account with a secure login.
- Add Password: Securely add new passwords.
- Update Password: Update existing passwords.
- Delete Password: Remove passwords that are no longer needed.
- Search Password: Search for passwords by name or other attributes.

## Project Structure

- src/: Contains all the Java source files.
- bin/: Directory for compiled Java classes.
- DatabaseHandler.java: Manages database connections and operations.
- User.java: Represents a user in the system.
- Password.java: Represents a password entry.
- EmailService.java: Handles email verification.
- Main.java: Entry point for the application.

## Database Schema

### Users table to store user information
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(10) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
	  encryption_key VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Passwords table to store the user's managed passwords
```sql
CREATE TABLE passwords (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    site_url VARCHAR(255) NOT NULL,
    site_name VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### Logs table to store user activity logs
```sql
CREATE TABLE logs (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    activity VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request with your changes.

## Contact

For any questions or suggestions, feel free to open an issue or contact me at [ankushgupta1806@gmail.com].
