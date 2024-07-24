package com.passwordManager;

public class LinkedListDSA {
    private User head;

    public void addUser(User user) {
        if (head == null) {
            head = user;
        } else {
            User current = head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(user);
        }
    }

    public User findUserByEmail(String email) {
        User current = head;
        while (current != null) {
            if (current.getEmail().equals(email)) {
                return current;
            }
            current = current.getNext();
        }
        return null;
    }
}
