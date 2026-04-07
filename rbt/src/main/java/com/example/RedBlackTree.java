package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedBlackTree extends AbstractTree {
    private static final Logger logger = LoggerFactory.getLogger(RedBlackTree.class);
    private static final boolean VALIDATE = false;

    @Override
    public boolean insert(int v) {
        Node z = new Node(v);
        Node y = null;
        Node x = root;

        while (x != null) {
            y = x;
            if (z.data == x.data) return false;
            x = (z.data < x.data) ? x.left : x.right;
        }

        z.parent = y;
        if (y == null) {
            root = z;
        } else if (z.data < y.data) {
            y.left = z;
        } else {
            y.right = z;
        }

        z.isRed = true;
        size++;
        fixAfterInsertion(z);

        if (VALIDATE) Validator.check(this);
        return true;
    }

    private void fixAfterInsertion(Node z) {
        while (z != root && z.parent.isRed) {
            if (z.parent == z.parent.parent.left) {
                Node y = z.parent.parent.right;
                if (y != null && y.isRed) {
                    z.parent.isRed = false;
                    y.isRed = false;
                    z.parent.parent.isRed = true;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.right) {
                        z = z.parent;
                        rotateLeft(z);
                    }
                    z.parent.isRed = false;
                    z.parent.parent.isRed = true;
                    rotateRight(z.parent.parent);
                }
            } else {
                // Symmetric cases (Parent is right child)
                Node y = z.parent.parent.left;
                if (y != null && y.isRed) {
                    z.parent.isRed = false;
                    y.isRed = false;
                    z.parent.parent.isRed = true;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        rotateRight(z);
                    }
                    z.parent.isRed = false;
                    z.parent.parent.isRed = true;
                    rotateLeft(z.parent.parent);
                }
            }
        }
        root.isRed = false;
    }

    private void rotateLeft(Node x) {
        logger.debug("Rotating left at node: {}", x.data);
        Node y = x.right;
        x.right = y.left;
        if (y.left != null) y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == null) root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        y.left = x;
        x.parent = y;
    }

    private void rotateRight(Node y) {
        logger.debug("Rotating right at node: {}", y.data);
        Node x = y.left;
        y.left = x.right;
        if (x.right != null) x.right.parent = y;
        x.parent = y.parent;
        if (y.parent == null) root = x;
        else if (y == y.parent.right) y.parent.right = x;
        else y.parent.left = x;
        x.right = y;
        y.parent = x;
    }

    @Override
    public boolean delete(int v) {
        return false;
    }
}