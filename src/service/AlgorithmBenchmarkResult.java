package service;

import model.Location;

import java.util.ArrayList;
import java.util.List;

/** One algorithm's averaged benchmark on a single dataset copy. */
public final class AlgorithmBenchmarkResult {

    private final String algorithmName;
    private final double averageCompares;
    private final double averageTimeMs;
    private final List<Location> top10Preview;

    public AlgorithmBenchmarkResult(String algorithmName,
                                    double averageCompares,
                                    double averageTimeMs,
                                    List<Location> top10Preview) {
        this.algorithmName = algorithmName;
        this.averageCompares = averageCompares;
        this.averageTimeMs = averageTimeMs;
        this.top10Preview = new ArrayList<>(top10Preview);
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public double getAverageCompares() {
        return averageCompares;
    }

    public double getAverageTimeMs() {
        return averageTimeMs;
    }

    public List<Location> getTop10Preview() {
        return new ArrayList<>(top10Preview);
    }
}
