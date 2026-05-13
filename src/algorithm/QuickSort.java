package algorithm;

import model.Location;

import java.util.List;

/**
 * Quick sort (chapter23 style: first element pivot, low/high scan, then swap pivot).
 * Same partition logic as the int[] version, using {@link Location#compareTo(Location)}.
 */
public class QuickSort implements SortAlgorithm {

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
        if (list.length > 0) {
            quickSort(list, 0, list.length - 1);
        }
        for (int i = 0; i < list.length; i++) {
            locations.set(i, list[i]);
        }

        return System.nanoTime() - start;
    }

    private void quickSort(Location[] list, int first, int last) {
        if (last > first) {
            int pivotIndex = partition(list, first, last);
            quickSort(list, first, pivotIndex - 1);
            quickSort(list, pivotIndex + 1, last);
        }
    }

    /** Partition the array list[first..last] */
    private int partition(Location[] list, int first, int last) {
        Location pivot = list[first]; // Choose the first element as the pivot
        int low = first + 1; // Index for forward search
        int high = last; // Index for backward search

        while (high > low) {
            // Search forward from left
            while (low <= high && cmp(list[low], pivot) <= 0) {
                low++;
            }

            // Search backward from right
            while (low <= high && cmp(list[high], pivot) > 0) {
                high--;
            }

            // Swap two elements in the list
            if (high > low) {
                Location temp = list[high];
                list[high] = list[low];
                list[low] = temp;
            }
        }

        while (high > first && cmp(list[high], pivot) >= 0) {
            high--;
        }

        // Swap pivot with list[high]
        if (cmp(pivot, list[high]) > 0) {
            list[first] = list[high];
            list[high] = pivot;
            return high;
        } else {
            return first;
        }
    }

    @Override
    public String getName() {
        return "Quick Sort";
    }

    @Override
    public long getComparisonCount() {
        return comparisons;
    }
}
