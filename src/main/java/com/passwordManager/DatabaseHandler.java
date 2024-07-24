package com.passwordManager;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class DatabaseHandler {
    private LinkedListDSA<User> userList = new LinkedListDSA<>();

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
        String encryptionKey = generateEncryptionKey(); // Generate encryption key
        String query = "INSERT INTO users (name, phone_number, email, password, encryption_key) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phoneNumber);
            pstmt.setString(3, email);
            pstmt.setString(4, hashedPassword);
            pstmt.setString(5, encryptionKey); // Store encryption key
            pstmt.executeUpdate();
            User user = new User(name, email, hashedPassword);
            userList.add(user);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking email existence: " + e.getMessage());
        }
        return false;
    }

    public boolean authenticateUser(String email, String password) throws Exception {
        String query = "SELECT id, password, encryption_key FROM users WHERE email = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                String storedPasswordHash = rs.getString("password");
                String encryptionKey = rs.getString("encryption_key"); // Retrieve encryption key
                PasswordUtils.setEncryptionKey(encryptionKey); // Set encryption key for PasswordUtils
                boolean isAuthenticated = PasswordUtils.validatePassword(password, storedPasswordHash);

                if (isAuthenticated) {
                    logActivity(userId, "User logged in");
                }

                return isAuthenticated;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private String generateEncryptionKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128); // AES-128
            SecretKey secretKey = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating encryption key", e);
        }
    }

    public void storePassword(String email, String siteUrl, String siteName, String username, String encryptedPassword) {
        int userId = getUserIdByEmail(email);
        String encryptionKey = getUserEncryptionKeyByEmail(email);
        PasswordUtils.setEncryptionKey(encryptionKey); // Set encryption key before encrypting
        String encryptedPwd;
        try {
            encryptedPwd = PasswordUtils.encrypt(encryptedPassword);
        } catch (Exception e) {
            System.out.println("Error encrypting password: " + e.getMessage());
            return;
        }

        PasswordNode node = new PasswordNode(siteUrl, siteName, username, encryptedPwd);
        User user = userList.findUserByEmail(email);
        if (user != null) {
            user.addPasswordNode(node);
        }

        String query = "INSERT INTO passwords (user_id, site_url, site_name, username, password) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, siteUrl);
            pstmt.setString(3, siteName);
            pstmt.setString(4, username);
            pstmt.setString(5, encryptedPwd);
            pstmt.executeUpdate();
            logActivity(userId, "Added password for site: " + siteName);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean updatePassword(String email, String searchCriteria, String newPassword) {
        int userId = getUserIdByEmail(email);
        if (passwordExists(userId, searchCriteria)) {
            String encryptionKey = getUserEncryptionKeyByEmail(email);
            PasswordUtils.setEncryptionKey(encryptionKey); // Set encryption key before encrypting
            String encryptedPwd;
            try {
                encryptedPwd = PasswordUtils.encrypt(newPassword);
            } catch (Exception e) {
                System.out.println("Error encrypting password: " + e.getMessage());
                return false;
            }

            PasswordNode node = userList.findUserByEmail(email).getPasswordNodeHead();
            while (node != null) {
                if (node.getSiteUrl().equals(searchCriteria) || node.getSiteName().equals(searchCriteria)) {
                    node.setEncryptedPassword(encryptedPwd);
                    break;
                }
                node = node.next;
            }
            String query = "UPDATE passwords SET password = ? WHERE user_id = ? AND (site_url = ? OR site_name = ?)";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, encryptedPwd);
                pstmt.setInt(2, userId);
                pstmt.setString(3, searchCriteria);
                pstmt.setString(4, searchCriteria);
                pstmt.executeUpdate();
                logActivity(userId, "Updated password for site: " + searchCriteria);
                return true;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return false;
            }
        } else {
            System.out.println("No password found for the given criteria.");
            return false;
        }
    }

    public List<String> searchPassword(String email, String searchCriteria) {
        int userId = getUserIdByEmail(email);
        String encryptionKey = getUserEncryptionKeyByEmail(email);
        PasswordUtils.setEncryptionKey(encryptionKey); // Set encryption key before decrypting

        List<String> matchedSites = new ArrayList<>();
        String query = "SELECT site_url, site_name, username, password FROM passwords WHERE user_id = ? AND (site_url ILIKE ? OR site_name ILIKE ? OR username ILIKE ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, "%" + searchCriteria + "%");
            pstmt.setString(3, "%" + searchCriteria + "%");
            pstmt.setString(4, "%" + searchCriteria + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String siteUrl = rs.getString("site_url");
                String siteName = rs.getString("site_name");
                String username = rs.getString("username");
                String encryptedPassword = rs.getString("password");
                String decryptedPassword = PasswordUtils.decrypt(encryptedPassword);
                matchedSites.add(formatSiteData(siteUrl, siteName, username, decryptedPassword));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Error decrypting password: " + e.getMessage());
        }

        return matchedSites;
    }

    private String formatSiteData(String siteUrl, String siteName, String username, String decryptedPassword) {
        return String.format(
                "-------------------------------------------\n" +
                        "Site URL      : %s\n" +
                        "Site Name     : %s\n" +
                        "Username      : %s\n" +
                        "Password      : %s\n" +
                        "-------------------------------------------",
                siteUrl, siteName, username, decryptedPassword
        );
    }

    public boolean deletePassword(String email, String searchCriteria) {
        int userId = getUserIdByEmail(email);
        if (passwordExists(userId, searchCriteria)) {
            User user = userList.findUserByEmail(email);
            PasswordNode node = user.getPasswordNodeHead();
            while (node != null) {
                if (node.getSiteUrl().equals(searchCriteria) || node.getSiteName().equals(searchCriteria)) {
                    user.removePasswordNode(node);
                    break;
                }
                node = node.next;
            }
            String query = "DELETE FROM passwords WHERE user_id = ? AND (site_url = ? OR site_name = ?)";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, searchCriteria);
                pstmt.setString(3, searchCriteria);
                pstmt.executeUpdate();
                logActivity(userId, "Deleted password for site: " + searchCriteria);
                return true;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return false;
            }
        } else {
            System.out.println("No password found for the given criteria.");
            return false;
        }
    }

    private boolean passwordExists(int userId, String searchCriteria) {
        String query = "SELECT id FROM passwords WHERE user_id = ? AND (site_url = ? OR site_name = ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, searchCriteria);
            pstmt.setString(3, searchCriteria);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public int getUserIdByEmail(String email) {
        String query = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new SQLException("User not found.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String getUserNameByEmail(String email) {
        String query = "SELECT name FROM users WHERE email = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            } else {
                throw new SQLException("User not found.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String getUserPasswordByEmail(String email) {
        String query = "SELECT password FROM users WHERE email = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("password");
            } else {
                throw new SQLException("User not found.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String getUserEncryptionKeyByEmail(String email) {
        String query = "SELECT encryption_key FROM users WHERE email = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("encryption_key");
            } else {
                throw new SQLException("User not found.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void loadUserData(String email) {
        int userId = getUserIdByEmail(email);
        User user = userList.findUserByEmail(email);
        if (user == null) {
            user = new User(getUserNameByEmail(email), email, getUserPasswordByEmail(email));
            userList.add(user);
        }

        String query = "SELECT site_url, site_name, username, password FROM passwords WHERE user_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String siteUrl = rs.getString("site_url");
                String siteName = rs.getString("site_name");
                String username = rs.getString("username");
                String encryptedPassword = rs.getString("password");
                PasswordNode node = new PasswordNode(siteUrl, siteName, username, encryptedPassword);
                user.addPasswordNode(node);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void logActivity(int userId, String activity) {
        String query = "INSERT INTO logs (user_id, activity) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, activity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void printLog(String email) {
        int userId = getUserIdByEmail(email);
        String query = "SELECT activity, timestamp FROM logs WHERE user_id = ? ORDER BY timestamp DESC";
        StackDSA<String> userActionStack = new StackDSA<>();
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String activity = rs.getString("activity");
                Timestamp timestamp = rs.getTimestamp("timestamp");
                String formattedTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                String logEntry = formattedTimestamp + ": " + activity;
                userActionStack.push(logEntry);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        Log.printLog(userActionStack);
    }
}
