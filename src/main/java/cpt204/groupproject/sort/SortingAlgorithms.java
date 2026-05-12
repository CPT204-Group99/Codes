package cpt204.groupproject.sort;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Bubble Sort, Quick Sort (median-of-three pivot), and Merge Sort on arrays.
 * All methods sort {@code arr[lo..hi]} inclusive using {@code cmp}.
 */
public final class SortingAlgorithms {

    private SortingAlgorithms() {
    }

    public static <T> void bubbleSort(T[] arr, Comparator<? super T> cmp) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (cmp.compare(arr[j], arr[j + 1]) > 0) {
                    swap(arr, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    public static <T> void quickSort(T[] arr, Comparator<? super T> cmp) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        quickSortRange(arr, 0, arr.length - 1, cmp);
    }

    private static <T> void quickSortRange(T[] arr, int lo, int hi, Comparator<? super T> cmp) {
        while (lo < hi) {
            int p = partitionLomutoMedianOfThree(arr, lo, hi, cmp);
            if (p - lo < hi - p) {
                quickSortRange(arr, lo, p - 1, cmp);
                lo = p + 1;
            } else {
                quickSortRange(arr, p + 1, hi, cmp);
                hi = p - 1;
            }
        }
    }

    /**
     * Lomuto partition; pivot is median-of-three by {@code cmp} among {@code lo}, {@code mid}, {@code hi}.
     */
    private static <T> int partitionLomutoMedianOfThree(T[] arr, int lo, int hi, Comparator<? super T> cmp) {
        int mid = (lo + hi) >>> 1;
        Integer[] idx = {lo, mid, hi};
        Arrays.sort(idx, (a, b) -> cmp.compare(arr[a], arr[b]));
        int pivotIndex = idx[1];
        swap(arr, pivotIndex, hi);

        T pivot = arr[hi];
        int i = lo - 1;
        for (int j = lo; j < hi; j++) {
            if (cmp.compare(arr[j], pivot) <= 0) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, hi);
        return i + 1;
    }

    public static <T> void mergeSort(T[] arr, Comparator<? super T> cmp) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        @SuppressWarnings("unchecked")
        T[] aux = (T[]) new Object[arr.length];
        mergeSortRange(arr, aux, 0, arr.length - 1, cmp);
    }

    private static <T> void mergeSortRange(T[] arr, T[] aux, int lo, int hi, Comparator<? super T> cmp) {
        if (lo >= hi) {
            return;
        }
        int mid = (lo + hi) >>> 1;
        mergeSortRange(arr, aux, lo, mid, cmp);
        mergeSortRange(arr, aux, mid + 1, hi, cmp);
        merge(arr, aux, lo, mid, hi, cmp);
    }

    private static <T> void merge(T[] arr, T[] aux, int lo, int mid, int hi, Comparator<? super T> cmp) {
        System.arraycopy(arr, lo, aux, lo, hi - lo + 1);
        int i = lo;
        int j = mid + 1;
        int k = lo;
        while (i <= mid && j <= hi) {
            if (cmp.compare(aux[i], aux[j]) <= 0) {
                arr[k++] = aux[i++];
            } else {
                arr[k++] = aux[j++];
            }
        }
        while (i <= mid) {
            arr[k++] = aux[i++];
        }
        while (j <= hi) {
            arr[k++] = aux[j++];
        }
    }

    private static <T> void swap(T[] arr, int i, int j) {
        T t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }
}
