package com.example;

public interface ITree {
    boolean insert(int v);
    boolean delete(int v);
    boolean contains(int v);
    int[] inOrder();
    int height();
    int size();
}