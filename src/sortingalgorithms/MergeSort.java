package sortingalgorithms;

import model.Location;

import java.util.List;

public class MergeSort implements SortAlgorithm {

    private long comparisons;

    private int cmp(Location a, Location b) {
        comparisons++;
        return a.compareTo(b);
    }

    @Override
    public long sort(List<Location> locations) {
        comparisons = 0;
        long start = System.nanoTime();

        Location[] list = locations.toArray(new Location[0]);
        mergeSort(list);
        for (int i = 0; i < list.length; i++) {
            locations.set(i, list[i]);
        }

        return System.nanoTime() - start;
    }


    private void mergeSort(Location[] list) {
        if (list.length > 1) {
            Location[] firstHalf = new Location[list.length / 2];
            System.arraycopy(list, 0, firstHalf, 0, list.length / 2);
            mergeSort(firstHalf);

            int secondHalfLength = list.length - list.length / 2;
            Location[] secondHalf = new Location[secondHalfLength];
            System.arraycopy(list, list.length / 2, secondHalf, 0, secondHalfLength);
            mergeSort(secondHalf);

            merge(firstHalf, secondHalf, list);
        }
    }


    private void merge(Location[] list1, Location[] list2, Location[] temp) {
        int current1 = 0;
        int current2 = 0;
        int current3 = 0;

        while (current1 < list1.length && current2 < list2.length) {
            if (cmp(list1[current1], list2[current2]) < 0) {
                temp[current3++] = list1[current1++];
            } else {
                temp[current3++] = list2[current2++];
            }
        }

        while (current1 < list1.length) {
            temp[current3++] = list1[current1++];
        }

        while (current2 < list2.length) {
            temp[current3++] = list2[current2++];
        }
    }

    @Override
    public String getName() {
        return "Merge Sort";
    }

    @Override
    public long getComparisonCount() {
        return comparisons;
    }
}
