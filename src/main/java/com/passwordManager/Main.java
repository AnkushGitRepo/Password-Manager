package com.passwordManager;

import java.util.Scanner;

class Main {
    public static Scanner scanner = new Scanner(System.in);
    private static DatabaseHandler dbHandler = new DatabaseHandler();

    public static void main(String[] args) {
        boolean mainMenuLoop = true;
        while (mainMenuLoop) {
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline left-over
            switch (choice) {
                case 1:
                    registerNewUser();
                    break;
                case 2:
                    loginUser();
                    break;
                case 3:
                    mainMenuLoop = false;
                    break;
                default:
                    System.out.println("Invalid Choice, Please try again.");
            }
        }
    }

    private static void registerNewUser() {
        System.out.print("Enter your email address: ");
        String email = scanner.nextLine();

        boolean emailVerificationStatus = EmailVerification.verifyMail(email);
        if (emailVerificationStatus) {
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();

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
            int choice = scanner.nextInt();
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
        System.out.print("Enter Site URL or Site Name to search: ");
        String searchCriteria = scanner.nextLine();
        try {
            String password = dbHandler.searchPassword(email, searchCriteria);
            if (password != null) {
                System.out.println("Password: " + password);
            } else {
                System.out.println("No password found for the given criteria.");
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
    public static boolean containsNumber(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
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
}
