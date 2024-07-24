package com.passwordManager;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String email;
    private String password;
    private PasswordNode passwordNodeHead; // Head of the linked list of PasswordNode
    private User next; // Pointer to the next User in the linked list

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.passwordNodeHead = null;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public PasswordNode getPasswordNodeHead() {
        return passwordNodeHead;
    }

    public void addPasswordNode(PasswordNode node) {
        if (passwordNodeHead == null) {
            passwordNodeHead = node;
        } else {
            PasswordNode current = passwordNodeHead;
            while (current.next != null) {
                current = current.next;
            }
            current.next = node;
        }
    }

    public void removePasswordNode(PasswordNode node) {
        if (passwordNodeHead == node) {
            passwordNodeHead = node.next;
        } else {
            PasswordNode current = passwordNodeHead;
            while (current.next != null) {
                if (current.next == node) {
                    current.next = node.next;
                    break;
                }
                current = current.next;
            }
        }
    }

    public User getNext() {
        return next;
    }

    public void setNext(User next) {
        this.next = next;
    }
}
