package service;

import algorithm.BubbleSort;
import algorithm.MergeSort;
import algorithm.QuickSortFirst;
import algorithm.QuickSortLast;
import algorithm.QuickSortMedianOfThree;
import algorithm.QuickSortMiddle;
import algorithm.QuickSortRandom;
import algorithm.SortAlgorithm;
import model.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Task A: runs sort algorithms on copies of the same list (fair comparison).
 */
public class SortingService {

    /**
     * Bubble, five quick-sort variants (separate classes per pivot rule), then merge.
     */
    public List<SortAlgorithm> allAlgorithms() {
        return Arrays.asList(
                new BubbleSort(),
                new QuickSortFirst(),
                new QuickSortLast(),
                new QuickSortMiddle(),
                new QuickSortRandom(),
                new QuickSortMedianOfThree(),
                new MergeSort());
    }

    public List<Location> copyList(List<Location> source) {
        return new ArrayList<>(source);
    }

    public static boolean isSorted(List<Location> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).compareTo(list.get(i + 1)) > 0) {
                return false;
            }
        }
        return true;
    }

    public static List<Location> topN(List<Location> sorted, int n) {
        int end = Math.min(n, sorted.size());
        return sorted.subList(0, end);
    }
}
