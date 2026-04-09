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

        
        runTest("Simple BST (Random)", randomData, false);
        runTest("Red-Black Tree (Random)", randomData, true);
        runMergeSortBenchmark("Random", randomData);

        runTest("Simple BST (Nearly Sorted 1%)", nearlySorted1, false);
        runTest("Red-Black Tree (Nearly Sorted 1%)", nearlySorted1, true);
        runMergeSortBenchmark("Nearly Sorted 1%", nearlySorted1);

        runTest("Simple BST (Nearly Sorted 5%)", nearlySorted2, false);
        runTest("Red-Black Tree (Nearly Sorted 5%)", nearlySorted2, true);
        runMergeSortBenchmark("Nearly Sorted 5%", nearlySorted2);

        runTest("Simple BST (Nearly Sorted 10%)", nearlySorted3, false);
        runTest("Red-Black Tree (Nearly Sorted 10%)", nearlySorted3, true);
        runMergeSortBenchmark("Nearly Sorted 10%", nearlySorted3);
    }

    private static final long SEED = 42;

    private static int[] generateNearlySorted(int n, double percent) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) arr[i] = i;

        Random r = new Random(SEED);
        int swaps = (int)(n * (percent / 100.0));
        for (int i = 0; i < swaps; i++) {
            int i1 = r.nextInt(n), i2 = r.nextInt(n);
            int temp = arr[i1]; arr[i1] = arr[i2]; arr[i2] = temp;
        }
        return arr;
    }

    private static int[] generateRandom(int n) {
        int[] arr = new int[n];
        Random r = new Random(SEED);
        for (int i = 0; i < n; i++) arr[i] = r.nextInt(n * 10);
        return arr;
    }

    private static void runTest(String label, int[] data, boolean useRBT) {
        long[] insertTimes = new long[ITERATIONS];
        long[] containsTimes = new long[ITERATIONS];
        long[] deleteTimes = new long[ITERATIONS];
        long[] treeSortTimes = new long[ITERATIONS]; 
        int height = 0;

        Random r = new Random(SEED);
        
        //(50k hits, 50k misses)
        int[] searchData = new int[100000];
        for(int i=0; i<50000; i++) searchData[i] = data[r.nextInt(N)]; 
        for(int i=50000; i<100000; i++) searchData[i] = -1 - r.nextInt(1000); 

        //Deletion Data (20%)
        int deleteCount = (int)(N * 0.2);
        int[] shuffleCopy = data.clone();
        for (int i = 0; i < deleteCount; i++) {
            int randomIndex = i + r.nextInt(N - i);
            int temp = shuffleCopy[randomIndex];
            shuffleCopy[randomIndex] = shuffleCopy[i];
            shuffleCopy[i] = temp;
        }
        int[] deleteData = Arrays.copyOfRange(shuffleCopy, 0, deleteCount);

        for (int i = 0; i < ITERATIONS; i++) {
            AbstractTree tree = useRBT ? new RedBlackTree() : new SimpleBST();
            
            long startInsert = System.nanoTime();
            for (int val : data) tree.insert(val);
            long endInsert = System.nanoTime();
            insertTimes[i] = endInsert - startInsert;

            if (i == ITERATIONS - 1) height = tree.height();

            long startInOrder = System.nanoTime();
            tree.inOrder();
            long endInOrder = System.nanoTime();
            treeSortTimes[i] = (endInsert - startInsert) + (endInOrder - startInOrder);

            long startC = System.nanoTime();
            for (int val : searchData) tree.contains(val);
            containsTimes[i] = System.nanoTime() - startC;

            long startD = System.nanoTime();
            for (int val : deleteData) tree.delete(val);
            deleteTimes[i] = System.nanoTime() - startD;
        }

        printStats(label, insertTimes, containsTimes, deleteTimes, treeSortTimes, height);
    }

    private static void printStats(String label, long[] insert, long[] contains, long[] delete, long[] treeSort, int height) {
        logger.info("=== {} ===", label);
        logger.info("Height: {}", height);
        
        displayMetric("Insert", insert);
        displayMetric("Contains (100k)", contains);
        displayMetric("Delete (20k)", delete);
        displayMetric("Tree Sort (Build+InOrder)", treeSort);
        logger.info("---------------------------\n");
    }

    private static void displayMetric(String name, long[] rawTimes) {
        long[] times = Arrays.copyOfRange(rawTimes, 1, rawTimes.length);
        Arrays.sort(times);

        double mean = Arrays.stream(times).average().orElse(0) / 1_000_000.0;
        double median = times[times.length / 2] / 1_000_000.0;
        double stdDev = calculateStdDev(times, mean * 1_000_000.0) / 1_000_000.0;

        logger.info("  {}: Mean: {}ms, Median: {}ms, StdDev: {}ms", 
                    name, String.format("%.2f", mean), String.format("%.2f", median), String.format("%.2f", stdDev));
    }

    private static double calculateStdDev(long[] data, double mean) {
        double sum = 0;
        for (long val : data) sum += Math.pow(val - mean, 2);
        return Math.sqrt(sum / data.length);
    }

    private static void runMergeSortBenchmark(String label, int[] data) {
        long[] times = new long[ITERATIONS];

        for (int i = 0; i < ITERATIONS; i++) {
            int[] copy = data.clone();
            long start = System.nanoTime();
            MergeSort.sort(copy);
            times[i] = System.nanoTime() - start;
        }

        displayMetric(label + " (Merge Sort)", times);
    }

    

 
}