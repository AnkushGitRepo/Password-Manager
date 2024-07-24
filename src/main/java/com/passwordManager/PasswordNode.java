package com.passwordManager;

public class PasswordNode {
    String siteUrl;
    String siteName;
    String username;
    String encryptedPassword;
    PasswordNode next; // Pointer to the next PasswordNode in the linked list

    public PasswordNode(String siteUrl, String siteName, String username, String encryptedPassword) {
        this.siteUrl = siteUrl;
        this.siteName = siteName;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.next = null;
    }

    // Getters and Setters
    public String getSiteUrl() {
        return siteUrl;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

}
