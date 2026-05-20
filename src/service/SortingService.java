package service;

import sortingalgorithms.BubbleSort;
import sortingalgorithms.MergeSort;
import sortingalgorithms.QuickSortFirst;
import sortingalgorithms.QuickSortLast;
import sortingalgorithms.QuickSortMedianOfThree;
import sortingalgorithms.QuickSortMiddle;
import sortingalgorithms.QuickSortRandom;
import sortingalgorithms.SortAlgorithm;
import model.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SortingService {

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
