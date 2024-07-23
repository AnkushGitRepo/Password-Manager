package com.passwordManager;

import java.sql.*;

public class DatabaseHandler {

    private Connection connect() {
        String url = "jdbc:postgresql://localhost:5432/secure"; // Update with your DB details
        String user = "postgres";
        String password = "1806";

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }
    }

    public void storeUser(String name, String phoneNumber, String email, String hashedPassword) {
        String query = "INSERT INTO users (name, phone_number, email, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phoneNumber);
            pstmt.setString(3, email);
            pstmt.setString(4, hashedPassword);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean authenticateUser(String email, String password) throws Exception {
        String query = "SELECT password FROM users WHERE email = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPasswordHash = rs.getString("password");
                return PasswordUtils.validatePassword(password, storedPasswordHash);
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void storePassword(String email, String siteUrl, String siteName, String username, String encryptedPassword) {
        String query = "INSERT INTO passwords (user_id, site_url, site_name, username, password) VALUES ((SELECT id FROM users WHERE email = ?), ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, siteUrl);
            pstmt.setString(3, siteName);
            pstmt.setString(4, username);
            pstmt.setString(5, encryptedPassword);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String searchPassword(String email, String searchCriteria) {
        String query = "SELECT password FROM passwords WHERE user_id = (SELECT id FROM users WHERE email = ?) AND (site_url = ? OR site_name = ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, searchCriteria);
            pstmt.setString(3, searchCriteria);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String encryptedPassword = rs.getString("password");
                return PasswordUtils.decrypt(encryptedPassword);
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean updatePassword(String email, String searchCriteria, String encryptedPassword) {
        String query = "UPDATE passwords SET password = ? WHERE user_id = (SELECT id FROM users WHERE email = ?) AND (site_url = ? OR site_name = ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, encryptedPassword);
            pstmt.setString(2, email);
            pstmt.setString(3, searchCriteria);
            pstmt.setString(4, searchCriteria);
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean deletePassword(String email, String searchCriteria) {
        String query = "DELETE FROM passwords WHERE user_id = (SELECT id FROM users WHERE email = ?) AND (site_url = ? OR site_name = ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            pstmt.setString(2, searchCriteria);
            pstmt.setString(3, searchCriteria);
            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
