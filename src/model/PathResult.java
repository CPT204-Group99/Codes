package model;

import java.util.ArrayList;
import java.util.List;

public class PathResult {

    private final String caseName;
    private final String startLocationId;
    private final String destinationLocationId;
    private final List<String> viaLocationIds;
    private final List<String> path;
    private final double totalCost;
    /** Sum of {@link System#nanoTime()} spans for shortest-path-tree builds (Dijkstra or Bellman-Ford) over all segments. */
    private final long algorithmElapsedNanos;

    public PathResult(String caseName,
                      String startLocationId,
                      String destinationLocationId,
                      List<String> viaLocationIds,
                      List<String> path,
                      double totalCost,
                      long algorithmElapsedNanos) {
        this.caseName = caseName;
        this.startLocationId = startLocationId;
        this.destinationLocationId = destinationLocationId;
        this.viaLocationIds = new ArrayList<>(viaLocationIds);
        this.path = new ArrayList<>(path);
        this.totalCost = totalCost;
        this.algorithmElapsedNanos = algorithmElapsedNanos;
    }

    public String getCaseName() {
        return caseName;
    }

    public String getStartLocationId() {
        return startLocationId;
    }

    public String getDestinationLocationId() {
        return destinationLocationId;
    }

    public List<String> getViaLocationIds() {
        return new ArrayList<>(viaLocationIds);
    }

    public List<String> getPath() {
        return new ArrayList<>(path);
    }

    public double getTotalCost() {
        return totalCost;
    }

    public long getAlgorithmElapsedNanos() {
        return algorithmElapsedNanos;
    }
}
