package com.example;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTree implements ITree {
    protected Node root;
    protected int size = 0;

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean contains(int v) {
        Node current = root;
        while (current != null) {
            if (v == current.data) return true;
            current = (v < current.data) ? current.left : current.right;
        }
        return false;
    }

    @Override
    public int[] inOrder() {
        List<Integer> list = new ArrayList<>();
        inOrderRecursive(root, list);
        return list.stream().mapToInt(i -> i).toArray();
    }

    private void inOrderRecursive(Node node, List<Integer> list) {
        if (node == null) return;
        inOrderRecursive(node.left, list);
        list.add(node.data);
        inOrderRecursive(node.right, list);
    }

    @Override
    public int height() {
        return getHeight(root);
    }

    private int getHeight(Node node) {
        if (node == null) return 0;
        return 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }


    @Override
    public abstract boolean insert(int v);

    @Override
    public abstract boolean delete(int v);
}