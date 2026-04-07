package com.example;

class Node {
    int data;
    Node left, right, parent;
    boolean isRed;

    public Node(int data) {
        this.data = data;
        this.isRed = true;
    }
}