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

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public PasswordNode getNext() {
        return next;
    }

    public void setNext(PasswordNode next) {
        this.next = next;
    }
}
