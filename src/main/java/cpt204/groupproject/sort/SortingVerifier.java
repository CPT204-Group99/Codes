package cpt204.groupproject.sort;

import java.util.Comparator;

/**
 * Checks that an array is sorted according to the comparator (non-decreasing order in {@code cmp}).
 */
public final class SortingVerifier {

    private SortingVerifier() {
    }

    public static <T> boolean isSorted(T[] arr, Comparator<? super T> cmp) {
        if (arr == null || arr.length <= 1) {
            return true;
        }
        for (int i = 1; i < arr.length; i++) {
            if (cmp.compare(arr[i - 1], arr[i]) > 0) {
                return false;
            }
        }
        return true;
    }
}
