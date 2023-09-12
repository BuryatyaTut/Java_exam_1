package info.kgeorgiy.ja.rakhmani;


import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        List<Integer> a = new ArrayList<>();

        int n = 10;
        int numberOfThreads = 5;


        for (int i = 0; i < n; ++i) {
            a.add(n - i);
        }

        for (int i = 0; i < n; ++i) {
            System.out.print(a.get(i) + " ");
        }
        System.out.println("\n");

        ParallelMergeSort<Integer> mergeSort = new ParallelMergeSort<>(a, Integer::compare, numberOfThreads);
        mergeSort.sort();

        for (int i = 0; i < n; ++i) {
            System.out.print(a.get(i) + " ");
        }

    }
}
