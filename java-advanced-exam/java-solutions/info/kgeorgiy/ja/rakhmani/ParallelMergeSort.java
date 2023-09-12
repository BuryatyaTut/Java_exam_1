package info.kgeorgiy.ja.rakhmani;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ParallelMergeSort<E extends Comparable<E>> {
    // :NOTE: Excess initialization -
    private List<E> list = new ArrayList<>();
    private Comparator<? super E> comparator = null;
    private int size = 0;

    private int maxThreads = 1;
    private int threadsCnt = 0;

    public ParallelMergeSort(List<E> list, Comparator<? super E> comparator, int threads) {
        this.list = list;
        this.comparator = comparator;
        this.maxThreads = threads;
        this.size = list.size();
    }

    // :NOTE: value is returned and return
    public List<E> sort() throws InterruptedException {
        int threadsCnt = Math.max(1, Math.min(list.size(), maxThreads));

        List<Thread> workers = new ArrayList<>();

        List<Integer> starts = new ArrayList<>();
        List<Integer> ends = new ArrayList<>();
        int remainder = list.size() % threadsCnt;
        int partSize = list.size() / threadsCnt;

        // May use Stream API exchange using for (but if you exactly sure that you are right then you may don't do this)
        for (int i = 0, left = 0; i < threadsCnt; i++) {
            // :NOTE: I don't understand, it may write more simple
            int right = left + partSize + (remainder-- >= 1 ? 1 : 0);
            int finalLeft = left;

            starts.add(finalLeft);
            ends.add(right - 1);

            workers.add(new Thread(() -> {
                try {
                    mergeSort(finalLeft, right - 1);
                } catch (InterruptedException e) {
                    // Think about exceptions, it's not okey if thread catch interruptedException and don't stop
                    e.printStackTrace();
                }
            }));

            workers.get(i).start();
            left = right;
        }

        // May use Stream API
        for (int i = 0; i < threadsCnt; i++) {
            workers.get(i).join();
        }
        mergeAll(starts, ends);
        return list;
    }

    private void mergeAll(List<Integer> starts, List<Integer> ends) {
        List<E> tmp = new ArrayList<>();
        while (someoneNotEnd(starts, ends)) {
            tmp.add(findMin(starts, ends));
        }

        for (int ind = 0; ind < tmp.size(); ++ind) {
            list.set(ind, tmp.get(ind));
        }
    }

    E findMin(List<Integer> starts, List<Integer> ends) {
        E ans = null;
        int ind = 0;
        for (int i = 0; i < starts.size(); ++i) {
            if (starts.get(i) <= ends.get(i)) {
                E x = list.get(starts.get(i));
                if (ans == null || comparator.compare(x, ans) < 0) {
                    ans = x;
                    ind = i;
                }
            }
        }

        starts.set(ind, starts.get(ind) + 1);
        return ans;
    }

    // I don't understand what is doing function by it's name
    boolean someoneNotEnd(List<Integer> starts, List<Integer> ends) {
        // May user stream API and foreach
        for (int i = 0; i < starts.size(); ++i) {
            if (starts.get(i) <= ends.get(i)) {
                return true;
            }
        }
        return false;
    }
// delete excess spaces


    // why you throw Interrupted exception - this exception for threads, not for this functions
    public void mergeSort(int l, int r) throws InterruptedException {
        if (r == l) {
            return;
        }

        mergeSort(l, l + (r - l) / 2);
        mergeSort(l + ((r - l) / 2) + 1, r);

        merge(l, l + (r - l) / 2, l + ((r - l) / 2) + 1, r);
    }


    private void merge(int l1, int r1, int l2, int r2) {
        List<E> tmp = new ArrayList<>();

        int i = l1;
        int j = l2;
        while (i <= r1 || j <= r2) {
            if (j > r2 || comparator.compare(list.get(i), list.get(j)) < 0) {
                tmp.add(list.get(i));
                i++;
            } else {
                tmp.add(list.get(j));
                j++;
            }
        }

        // :NOTE: You may use Stream API
        for (int ind = 0; ind < tmp.size(); ++ind) {
            list.set(l1 + ind, tmp.get(ind));
        }
    }
}
