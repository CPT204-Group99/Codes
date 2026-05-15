package sortingalgorithms;

import model.Location;

import java.util.List;


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

    private void bubbleSort(Location[] list) {
        boolean needNextPass = true;

        for (int k = 1; k < list.length && needNextPass; k++) {
            needNextPass = false;
            for (int i = 0; i < list.length - k; i++) {
                if (cmp(list[i], list[i + 1]) > 0) {
                    Location temp = list[i];
                    list[i] = list[i + 1];
                    list[i + 1] = temp;

                    needNextPass = true;
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
