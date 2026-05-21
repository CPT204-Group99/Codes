package service;

import model.Location;

import java.util.ArrayList;
import java.util.List;

/** Benchmark results for one dataset plus the Top-10 list passed to Task B. */
public final class DatasetSortingResult {

    private final List<AlgorithmBenchmarkResult> algorithmResults;
    private final List<Location> top10ForTaskB;

    public DatasetSortingResult(List<AlgorithmBenchmarkResult> algorithmResults, List<Location> top10ForTaskB) {
        this.algorithmResults = new ArrayList<>(algorithmResults);
        this.top10ForTaskB = new ArrayList<>(top10ForTaskB);
    }

    public List<AlgorithmBenchmarkResult> getAlgorithmResults() {
        return new ArrayList<>(algorithmResults);
    }

    public List<Location> getTop10ForTaskB() {
        return new ArrayList<>(top10ForTaskB);
    }
}
