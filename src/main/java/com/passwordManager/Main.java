package com.passwordManager;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

class Main {
    public static Scanner scanner = new Scanner(System.in);
    private static DatabaseHandler dbHandler = new DatabaseHandler();

    public static void main(String[] args) {
        displayWelcomeMessage();
        boolean mainMenuLoop = true;
        while (mainMenuLoop) {
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = getInputInt();
            scanner.nextLine(); // Consume newline left-over
            switch (choice) {
                case 1:
                    registerNewUser();
                    break;
                case 2:
                    loginUser();
                    break;
                case 3:
                    displayClosingMessage();
                    mainMenuLoop = false;
                    break;
                default:
                    System.out.println("Invalid Choice, Please try again.");
            }
        }
    }

    private static void displayWelcomeMessage() {
        System.out.println("\n=======================================");
        System.out.println("  Welcome to the Password Manager App  ");
        System.out.println("=======================================");
        System.out.println("Manage your passwords securely and easily.");
        System.out.println("Please select an option from the menu below:\n");
    }

    private static void displayClosingMessage() {
        System.out.println("\n=======================================");
        System.out.println("   Thank you for using our application  ");
        System.out.println("=======================================");
        System.out.println("Goodbye! Stay secure!");
    }

    private static void registerNewUser() {
        System.out.print("Enter your email address: ");
        String email = scanner.nextLine();

        boolean emailVerificationStatus = EmailVerification.verifyMail(email);
        if (emailVerificationStatus) {
            String name = null;
            boolean nameLoop = true;
            while(nameLoop) {
                System.out.print("Enter your name: ");
                name = scanner.nextLine();
                if (isValidName(name)) {
                    nameLoop = false;
                }
                else{
                    System.out.println("Invalid Name As Input, Please Try Again.");
                }
            }


            String phoneNumber = getValidPhoneNumber();
            String password = getPassword();

            try {
                String hashedPassword = PasswordUtils.generateStrongPasswordHash(password);
                dbHandler.storeUser(name, phoneNumber, email, hashedPassword);
                System.out.println("User registered successfully.");
                showPasswordManagerMenu(email);
            } catch (Exception e) {
                System.out.println("Error storing password: " + e.getMessage());
            }
        } else {
            System.out.println("Please try again.");
        }
    }

    private static void loginUser() {
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();

        // Check if the email exists in the database
        if (!dbHandler.emailExists(email)) {
            System.out.println("Email does not exist. Please register first.");
            return;
        }

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        try {
            boolean isAuthenticated = dbHandler.authenticateUser(email, password);
            if (isAuthenticated) {
                System.out.println("Login successful.");
                dbHandler.loadUserData(email); // Load user data from database
                showPasswordManagerMenu(email); // Pass email to identify the user
            } else {
                System.out.println("Invalid email or password.");
            }
        } catch (Exception e) {
            System.out.println("Error during authentication: " + e.getMessage());
        }
    }

    private static void showPasswordManagerMenu(String email) {
        boolean passwordManagerLoop = true;
        while (passwordManagerLoop) {
            System.out.println("1. Add Password");
            System.out.println("2. Search Password");
            System.out.println("3. Update Password");
            System.out.println("4. Delete Password");
            System.out.println("5. Show Recent Activities");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");
            int choice = getInputInt();
            scanner.nextLine(); // Consume newline left-over
            switch (choice) {
                case 1:
                    addPassword(email);
                    break;
                case 2:
                    searchPassword(email);
                    break;
                case 3:
                    updatePassword(email);
                    break;
                case 4:
                    deletePassword(email);
                    break;
                case 5:
                    dbHandler.printLog(email);
                    break;
                case 6:
                    passwordManagerLoop = false;
                    break;
                default:
                    System.out.println("Invalid Choice, Please try again.");
            }
        }
    }

    private static void addPassword(String email) {
        System.out.print("Enter Site URL: ");
        String siteUrl = scanner.nextLine();
        System.out.print("Enter Site Name: ");
        String siteName = scanner.nextLine();
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        try {
            dbHandler.storePassword(email, siteUrl, siteName, username, password);
            System.out.println("Password added successfully.");
        } catch (Exception e) {
            System.out.println("Error storing password: " + e.getMessage());
        }
    }

    private static void searchPassword(String email) {
        System.out.print("Enter Site URL, Site Name, Username, or Initials to search: ");
        String searchCriteria = scanner.nextLine();
        try {
            List<String> matchedSites = dbHandler.searchPassword(email, searchCriteria);
            if (!matchedSites.isEmpty()) {
                System.out.println("Matched Sites:");
                for (String site : matchedSites) {
                    System.out.println(site);
                }
            } else {
                System.out.println("No matches found for the given criteria.");
            }
        } catch (Exception e) {
            System.out.println("Error retrieving password: " + e.getMessage());
        }
    }

    private static void updatePassword(String email) {
        System.out.print("Enter Site URL or Site Name to update: ");
        String searchCriteria = scanner.nextLine();
        System.out.print("Enter New Password: ");
        String newPassword = scanner.nextLine();

        try {
            dbHandler.updatePassword(email, searchCriteria, newPassword);
            System.out.println("Password updated successfully.");
        } catch (Exception e) {
            System.out.println("Error updating password: " + e.getMessage());
        }
    }

    private static void deletePassword(String email) {
        System.out.print("Enter Site URL or Site Name to delete: ");
        String searchCriteria = scanner.nextLine();
        try {
            boolean isDeleted = dbHandler.deletePassword(email, searchCriteria);
            if (isDeleted) {
                System.out.println("Password deleted successfully.");
            } else {
                System.out.println("No password found for the given criteria.");
            }
        } catch (Exception e) {
            System.out.println("Error deleting password: " + e.getMessage());
        }
    }

    private static String getValidPhoneNumber() {
        String phoneNumber;
        while (true) {
            System.out.print("Enter your number: ");
            phoneNumber = scanner.nextLine();
            if (isValidPhoneNumber(phoneNumber)) {
                break;
            } else {
                System.out.println("Invalid input in number! Please try again.");
            }
        }
        return phoneNumber;
    }

    private static String getPassword() {
        System.out.print("Set password for account: ");
        return scanner.nextLine();
    }

    // To check if the given string contains a number or not
    public static boolean isValidName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        for (char c : name.toCharArray()) {
            if (!Character.isLetter(c) && c != ' ') {
                return false;
            }
        }
        return true;
    }

    // To check if the given phone number is valid or not
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() != 10) {
            System.out.println("Number cannot be null and must contain 10 digits.");
            return false;
        }
        for (char c : phoneNumber.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    private static int getInputInt() {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter a valid number: ");
                scanner.nextLine(); // Consume invalid input
            }
        }
    }
}
