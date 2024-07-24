package com.passwordManager;

public class PasswordLinkedList {
    private PasswordNode head;

    public void addPassword(String siteUrl, String siteName, String username, String encryptedPassword) {
        PasswordNode newNode = new PasswordNode(siteUrl, siteName, username, encryptedPassword);
        if (head == null) {
            head = newNode;
        } else {
            PasswordNode current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        Log.logActivity("Added password for site: " + siteName);
    }

    public PasswordNode searchPassword(String searchCriteria) {
        PasswordNode current = head;
        while (current != null) {
            if (current.siteUrl.equals(searchCriteria) || current.siteName.equals(searchCriteria)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    public boolean updatePassword(String searchCriteria, String newEncryptedPassword) {
        PasswordNode current = head;
        while (current != null) {
            if (current.siteUrl.equals(searchCriteria) || current.siteName.equals(searchCriteria)) {
                current.encryptedPassword = newEncryptedPassword;
                Log.logActivity("Updated password for site: " + current.siteName);
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public boolean deletePassword(String searchCriteria) {
        if (head == null) return false;
        if (head.siteUrl.equals(searchCriteria) || head.siteName.equals(searchCriteria)) {
            head = head.next;
            Log.logActivity("Deleted password for site: " + searchCriteria);
            return true;
        }

        PasswordNode current = head;
        while (current.next != null) {
            if (current.next.siteUrl.equals(searchCriteria) || current.next.siteName.equals(searchCriteria)) {
                current.next = current.next.next;
                Log.logActivity("Deleted password for site: " + searchCriteria);
                return true;
            }
            current = current.next;
        }
        return false;
    }
}
