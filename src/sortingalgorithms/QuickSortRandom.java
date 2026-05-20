package sortingalgorithms;

import model.Location;

import java.util.List;
import java.util.Random;


public class QuickSortRandom implements SortAlgorithm {

    private final Random random = new Random();
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
            int r = first + random.nextInt(last - first + 1);
            swap(list, first, r);
            int pivotIndex = partition(list, first, last);
            quickSort(list, first, pivotIndex - 1);
            quickSort(list, pivotIndex + 1, last);
        }
    }

    private void swap(Location[] list, int i, int j) {
        Location t = list[i];
        list[i] = list[j];
        list[j] = t;
    }

    private int partition(Location[] list, int first, int last) {
        Location pivot = list[first];
        int low = first + 1;
        int high = last;

        while (high > low) {
            while (low <= high && cmp(list[low], pivot) <= 0) {
                low++;
            }
            while (low <= high && cmp(list[high], pivot) > 0) {
                high--;
            }
            if (high > low) {
                Location temp = list[high];
                list[high] = list[low];
                list[low] = temp;
            }
        }

        while (high > first && cmp(list[high], pivot) >= 0) {
            high--;
        }

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
        return "Quick Sort (pivot: random)";
    }

    @Override
    public long getComparisonCount() {
        return comparisons;
    }
}
