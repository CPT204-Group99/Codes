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

    /** Warm-up sorts per algorithm (JVM / class loading); not included in reported time. */
    public static final int WARMUP_RUNS = 50;

    /** Timed runs per algorithm; wall time and compareTo counts are averaged over this many. */
    public static final int MEASURED_RUNS = 1000;

    private static final int TOP_N = 10;

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
        return new ArrayList<>(sorted.subList(0, end));
    }

    /**
     * Top-10 for Task B: one sort using the last registered algorithm (currently Merge Sort).
     * Benchmark loops above measure every algorithm; this selection is explicit and independent of them.
     */
    public List<Location> selectTop10ForTaskB(List<Location> source) {
        List<SortAlgorithm> algorithms = allAlgorithms();
        SortAlgorithm selector = algorithms.get(algorithms.size() - 1);
        List<Location> copy = copyList(source);
        selector.sort(copy);
        if (!isSorted(copy)) {
            throw new IllegalStateException(selector.getName() + " failed sort check for Task B Top-10");
        }
        return topN(copy, TOP_N);
    }

    public AlgorithmBenchmarkResult benchmarkAlgorithm(SortAlgorithm algorithm, List<Location> source) {
        for (int w = 0; w < WARMUP_RUNS; w++) {
            List<Location> warm = copyList(source);
            algorithm.sort(warm);
        }

        long totalNs = 0;
        long totalCompares = 0;
        List<Location> lastCopy = null;
        for (int m = 0; m < MEASURED_RUNS; m++) {
            List<Location> copy = copyList(source);
            long elapsedNs = algorithm.sort(copy);
            totalNs += elapsedNs;
            totalCompares += algorithm.getComparisonCount();
            lastCopy = copy;
        }

        if (lastCopy == null || !isSorted(lastCopy)) {
            throw new IllegalStateException(algorithm.getName() + " failed sort check");
        }

        double avgMs = (totalNs / (double) MEASURED_RUNS) / 1_000_000.0;
        double avgCompares = totalCompares / (double) MEASURED_RUNS;
        return new AlgorithmBenchmarkResult(
                algorithm.getName(),
                avgCompares,
                avgMs,
                topN(lastCopy, TOP_N));
    }

    public DatasetSortingResult benchmarkDataset(List<Location> source) {
        List<AlgorithmBenchmarkResult> results = new ArrayList<>();
        for (SortAlgorithm algorithm : allAlgorithms()) {
            results.add(benchmarkAlgorithm(algorithm, source));
        }
        return new DatasetSortingResult(results, selectTop10ForTaskB(source));
    }
}
