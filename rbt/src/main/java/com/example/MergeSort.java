package com.example;

public class MergeSort {
    public static void sort(int[] array) {
        if (array == null || array.length <= 1) return;
        int[] helper = new int[array.length];
        mergeSort(array, helper, 0, array.length - 1);
    }

    private static void mergeSort(int[] array, int[] helper, int low, int high) {
        if (low < high) {
            int mid = low + (high - low) / 2;
            mergeSort(array, helper, low, mid);
            mergeSort(array, helper, mid + 1, high);
            merge(array, helper, low, mid, high);
        }
    }

    private static void merge(int[] array, int[] helper, int low, int mid, int high) {
        for (int i = low; i <= high; i++) helper[i] = array[i];

        int i = low, j = mid + 1, k = low;
        while (i <= mid && j <= high) {
            if (helper[i] <= helper[j]) array[k++] = helper[i++];
            else array[k++] = helper[j++];
        }
        while (i <= mid) array[k++] = helper[i++];
    }
}
