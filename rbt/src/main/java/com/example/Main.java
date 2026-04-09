package com.example;

import java.util.Arrays;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int N = 100000;
    private static final int ITERATIONS = 6;

    public static void main(String[] args) {
        int[] randomData = generateRandom(N);
        int[] nearlySorted1 = generateNearlySorted(N, 1.0); 
        int[] nearlySorted2 = generateNearlySorted(N, 5.0); 
        int[] nearlySorted3 = generateNearlySorted(N, 10.0); 

        
        runTest("Red-Black Tree (Random)", randomData, true);
        runTest("Simple BST (Random)", randomData, false);
        
        
        runTest("Red-Black Tree (Nearly Sorted)", nearlySorted1, true);
        runTest("Simple BST (Nearly Sorted)", nearlySorted1, false);
        runTest("Red-Black Tree (Nearly Sorted)", nearlySorted2, true);
        runTest("Simple BST (Nearly Sorted)", nearlySorted2, false);
        runTest("Red-Black Tree (Nearly Sorted)", nearlySorted3, true);
        runTest("Simple BST (Nearly Sorted)", nearlySorted3, false);
    
    }

    private static void runTest(String label, int[] data, boolean useRBT) {
        long[] insertTimes = new long[ITERATIONS];
        long[] containsTimes = new long[ITERATIONS];
        long[] deleteTimes = new long[ITERATIONS];
        long[] inorderTimes = new long[ITERATIONS];
        int height = 0;

        int[] searchData = new int[100000];
        Random r = new Random();
        for(int i=0; i<50000; i++) searchData[i] = data[r.nextInt(N)]; // Present
        for(int i=50000; i<100000; i++) searchData[i] = -1 - r.nextInt(1000); // Not present 

        int deleteCount = (int)(N * 0.2);
        int[] deleteData = Arrays.copyOfRange(data, 0, deleteCount);

        for (int i = 0; i < ITERATIONS; i++) {
            AbstractTree tree = useRBT ? new RedBlackTree() : new SimpleBST();
            
            //Benchmark Insertion
            long start = System.nanoTime();
            for (int val : data) tree.insert(val);
            insertTimes[i] = System.nanoTime() - start;

            //Record height once 
            if (i == ITERATIONS - 1) height = tree.height();

            //Benchmark In-Order
            long inStart = System.nanoTime();
            tree.inOrder();
            inorderTimes[i] = System.nanoTime() - inStart;

            //Benchmark Contains
            long cStart = System.nanoTime();
            for (int val : searchData) tree.contains(val);
            containsTimes[i] = System.nanoTime() - cStart;

            //Benchmark Delete
            long dStart = System.nanoTime();
            for (int val : deleteData) tree.delete(val);
            deleteTimes[i] = System.nanoTime() - dStart;
        }

        printStats(label, insertTimes, containsTimes, deleteTimes, inorderTimes, height);
    }

    private static int[] generateNearlySorted(int n, double percent) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = i;
        Random r = new Random();
        int swaps = (int)(n * (percent / 100.0));
        for (int i = 0; i < swaps; i++) {
            int i1 = r.nextInt(n), i2 = r.nextInt(n);
            int temp = arr[i1]; arr[i1] = arr[i2]; arr[i2] = temp;
        }
        return arr;
    }

    private static int[] generateRandom(int n) {
        int[] arr = new int[n];
        Random r = new Random();
        for (int i = 0; i < n; i++) arr[i] = r.nextInt(n * 10);
        return arr;
    }

    private static void printStats(String label, long[] insert, long[] contains, long[] delete, long[] inorder, int height) {
        double mean = Arrays.stream(insert).average().orElse(0) / 1_000_000.0;
        double stdDev = calculateStdDev(insert, mean * 1_000_000.0) / 1_000_000.0;
        
        logger.info("=== {} ===", label);
        logger.info("Height: {}", height);
        logger.info("Insert Mean: {} ms (StdDev: {})", String.format("%.2f", mean), String.format("%.2f", stdDev));
        logger.info("Contains Mean (100k ops): {} ms", String.format("%.2f", (Arrays.stream(contains).average().orElse(0) / 1_000_000.0)));
        logger.info("Delete Mean (20k ops): {} ms", String.format("%.2f", (Arrays.stream(delete).average().orElse(0) / 1_000_000.0)));
        logger.info("Inorder Mean: {} ms", String.format("%.2f", (Arrays.stream(inorder).average().orElse(0) / 1_000_000.0)));
    }

    private static double calculateStdDev(long[] data, double mean) {
        double sum = 0;
        for (long val : data) sum += Math.pow(val - mean, 2);
        return Math.sqrt(sum / data.length);
    }
}