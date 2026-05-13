package algorithm;

import model.Location;

import java.util.List;

/**
 * Bubble sort (chapter23 style: needNextPass, outer k, inner i, swap with temp).
 * Works on {@link Location#compareTo(Location)} order via a backing array.
 */
public class BubbleSort implements SortAlgorithm {

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
        bubbleSort(list);
        for (int i = 0; i < list.length; i++) {
            locations.set(i, list[i]);
        }

        return System.nanoTime() - start;
    }

    /** Bubble sort method (same control flow as int[] textbook version). */
    private void bubbleSort(Location[] list) {
        boolean needNextPass = true;

        for (int k = 1; k < list.length && needNextPass; k++) {
            // Array may be sorted and next pass not needed
            needNextPass = false;
            for (int i = 0; i < list.length - k; i++) {
                if (cmp(list[i], list[i + 1]) > 0) {
                    // Swap list[i] with list[i + 1]
                    Location temp = list[i];
                    list[i] = list[i + 1];
                    list[i + 1] = temp;

                    needNextPass = true; // Next pass still needed
                }
            }
        }
    }

    @Override
    public String getName() {
        return "Bubble Sort";
    }

    @Override
    public long getComparisonCount() {
        return comparisons;
    }
}
