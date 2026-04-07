package com.example;

public class SimpleBST extends AbstractTree {

    @Override
    public boolean insert(int v) {
        if (root == null) {
            root = new Node(v);
            size++;
            return true;
        }
        return insertRecursive(root, v);
    }

    private boolean insertRecursive(Node current, int v) {
        if (v == current.data) {
            return false;
        }

        if (v < current.data) {
            if (current.left == null) {
                current.left = new Node(v);
                current.left.parent = current;
                size++;
                return true;
            }
            return insertRecursive(current.left, v);
        } else {
            if (current.right == null) {
                current.right = new Node(v);
                current.right.parent = current;
                size++;
                return true;
            }
            return insertRecursive(current.right, v);
        }
    }

    @Override
    public boolean delete(int v) {
        int initialSize = this.size;
        root = deleteRecursive(root, v);

        return this.size < initialSize;
    }

    private Node deleteRecursive(Node current, int v) {
        if (current == null) return null;

        if (v < current.data) {
            current.left = deleteRecursive(current.left, v);
        } else if (v > current.data) {
            current.right = deleteRecursive(current.right, v);
        } else {
            this.size--;

            if (current.left == null) return current.right;
            if (current.right == null) return current.left;

            current.data = minValue(current.right);

            current.right = deleteRecursive(current.right, current.data);

            this.size++;
        }
        return current;
    }

    private int minValue(Node node) {
        int minv = node.data;
        while (node.left != null) {
            minv = node.left.data;
            node = node.left;
        }
        return minv;
    }
}