package com.passwordManager;

public class LinkedListDSA<T> {
    private Node<T> head;

    // Add data to the end of the list
    public void add(T data) {
        if (head == null) {
            head = new Node<>(data);
        } else {
            Node<T> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = new Node<>(data);
        }
    }

    // Find if the list contains the specified data
    public boolean contains(T data) {
        Node<T> current = head;
        while (current != null) {
            if (current.data.equals(data)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // Remove specified data from the list
    public boolean remove(T data) {
        if (head == null) {
            return false;
        }
        if (head.data.equals(data)) {
            head = head.next;
            return true;
        }
        Node<T> current = head;
        while (current.next != null) {
            if (current.next.data.equals(data)) {
                current.next = current.next.next;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // Inner Node class
    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    // Additional method to find user by email
    public T findUserByEmail(String email) {
        Node<T> current = head;
        while (current != null) {
            if (((User) current.data).getEmail().equals(email)) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }
}
