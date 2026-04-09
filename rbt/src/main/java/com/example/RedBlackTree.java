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
        Node z = search(root, v);
        if (z == null) return false;

        Node x, y;
        y = z;
        boolean yOriginalColorIsRed = y.isRed;

        if (z.left == null) {
            x = z.right;
            transplant(z, z.right);
        } else if (z.right == null) {
            x = z.left;
            transplant(z, z.left);
        } else {
            y = minimum(z.right);
            yOriginalColorIsRed = y.isRed;
            x = y.right;
            if (y.parent == z) {
                if (x != null) x.parent = y;
            } else {
                transplant(y, y.right);
                y.right = z.right;
                if (y.right != null) y.right.parent = y;
            }
            transplant(z, y);
            y.left = z.left;
            if (y.left != null) y.left.parent = y;
            y.isRed = z.isRed;
        }

        size--;
        
        if (!yOriginalColorIsRed) {
            fixAfterDeletion(x, (x != null) ? x.parent : y.parent);
        }

        return true;
    }

    private void fixAfterDeletion(Node x, Node parent) {
        while (x != root && parent != null && (x == null || !x.isRed)) {
            if (x == parent.left) {
                Node s = parent.right; 
                if (isRed(s)) {
                    s.isRed = false;
                    parent.isRed = true;
                    rotateLeft(parent);
                    s = parent.right;
                }
                if (!isRed(getLeft(s)) && !isRed(getRight(s))) {
                    if (s != null) s.isRed = true;
                    x = parent;
                    parent = x.parent;
                } else {
                    if (!isRed(getRight(s))) {
                        if (getLeft(s) != null) getLeft(s).isRed = false;
                        if (s != null) s.isRed = true;
                        rotateRight(s);
                        s = parent.right;
                    }
                    if (s != null) s.isRed = parent.isRed;
                    parent.isRed = false;
                    if (getRight(s) != null) getRight(s).isRed = false;
                    rotateLeft(parent);
                    x = root;
                }
            } else {
                Node s = parent.left;
                if (isRed(s)) {
                    s.isRed = false;
                    parent.isRed = true;
                    rotateRight(parent);
                    s = parent.left;
                }
                if (!isRed(getRight(s)) && !isRed(getLeft(s))) {
                    if (s != null) s.isRed = true;
                    x = parent;
                    parent = x.parent;
                } else {
                    if (!isRed(getLeft(s))) {
                        if (getRight(s) != null) getRight(s).isRed = false;
                        if (s != null) s.isRed = true;
                        rotateLeft(s);
                        s = parent.left;
                    }
                    if (s != null) s.isRed = parent.isRed;
                    parent.isRed = false;
                    if (getLeft(s) != null) getLeft(s).isRed = false;
                    rotateRight(parent);
                    x = root;
                }
            }
        }
        if (x != null) x.isRed = false;
    }

    private boolean isRed(Node n) { return n != null && n.isRed; }
    private Node getLeft(Node n) { return (n == null) ? null : n.left; }
    private Node getRight(Node n) { return (n == null) ? null : n.right; }

    private void transplant(Node u, Node v) {
        if (u.parent == null) root = v;
        else if (u == u.parent.left) u.parent.left = v;
        else u.parent.right = v;
        if (v != null) v.parent = u.parent;
    }

    private Node search(Node node, int v) {
        while (node != null && v != node.data) {
            node = (v < node.data) ? node.left : node.right;
        }
        return node;
    }

    private Node minimum(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

    public static class Validator {
        public static void check(RedBlackTree tree) {
            if (tree.root == null) return;
            if (tree.root.isRed) throw new IllegalStateException("Root must be black");
            checkBlackHeight(tree.root);
            checkRedNodes(tree.root);
        }

        private static int checkBlackHeight(Node node) {
            if (node == null) return 1;
            int leftH = checkBlackHeight(node.left);
            int rightH = checkBlackHeight(node.right);
            if (leftH != rightH) throw new IllegalStateException("Black height mismatch");
            return leftH + (node.isRed ? 0 : 1);
        }

        private static void checkRedNodes(Node node) {
            if (node == null) return;
            if (node.isRed) {
                if ((node.left != null && node.left.isRed) || (node.right != null && node.right.isRed)) {
                    throw new IllegalStateException("Double red violation");
                }
            }
            checkRedNodes(node.left);
            checkRedNodes(node.right);
        }
    }

}