package service;

import graph.WeightedGraph;
import model.PathResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShortestPathService {

    public static final int DIJKSTRA_ARRAY = 0;
    public static final int BELLMAN_FORD = 1;
    public static final int DIJKSTRA_HEAP = 2;

    private final WeightedGraph<String> graph;

    public ShortestPathService(WeightedGraph<String> graph) {
        this.graph = graph;
    }

    public PathResult findPath(String caseName, String startId, String destinationId, int algorithmType) {
        return findPathVia(caseName, startId, destinationId, Collections.emptyList(), algorithmType);
    }

    public PathResult findPathVia(String caseName,
                                  String startId,
                                  String destinationId,
                                  List<String> orderedWaypoints,
                                  int algorithmType) {
        List<String> stops = new ArrayList<>();
        stops.add(startId);
        stops.addAll(orderedWaypoints);
        stops.add(destinationId);

        List<String> fullPath = new ArrayList<>();
        double totalCost = 0;
        long totalAlgorithmNanos = 0;

        for (int i = 0; i < stops.size() - 1; i++) {
            PathSegment segment = computeSegment(stops.get(i), stops.get(i + 1), algorithmType);
            totalCost += segment.cost;
            totalAlgorithmNanos += segment.algorithmElapsedNanos;
            appendSegment(fullPath, segment.path, i > 0);
        }

        return new PathResult(caseName, startId, destinationId, orderedWaypoints, fullPath, totalCost,
                totalAlgorithmNanos);
    }

    private PathSegment computeSegment(String startId, String destinationId, int algorithmType) {
        int startIndex = graph.getIndex(startId);
        int destinationIndex = graph.getIndex(destinationId);

        if (startIndex < 0) {
            throw new IllegalArgumentException("Unknown start location: " + startId);
        }
        if (destinationIndex < 0) {
            throw new IllegalArgumentException("Unknown destination location: " + destinationId);
        }

        long start = System.nanoTime();
        WeightedGraph<String>.ShortestPathTree tree = graph.shortestPathTree(startIndex, algorithmType);
        long algorithmElapsedNanos = System.nanoTime() - start;

        double cost = tree.getCost(destinationIndex);
        if (Double.isInfinite(cost)) {
            throw new IllegalStateException("No path found from " + startId + " to " + destinationId);
        }

        List<String> backwardPath = tree.getPath(destinationIndex);
        List<String> forwardPath = new ArrayList<>(backwardPath);
        Collections.reverse(forwardPath);
        return new PathSegment(forwardPath, cost, algorithmElapsedNanos);
    }

    private void appendSegment(List<String> fullPath, List<String> segmentPath, boolean skipFirst) {
        int startIndex = skipFirst ? 1 : 0;
        for (int i = startIndex; i < segmentPath.size(); i++) {
            fullPath.add(segmentPath.get(i));
        }
    }

    private static class PathSegment {

        private final List<String> path;
        private final double cost;
        private final long algorithmElapsedNanos;

        private PathSegment(List<String> path, double cost, long algorithmElapsedNanos) {
            this.path = path;
            this.cost = cost;
            this.algorithmElapsedNanos = algorithmElapsedNanos;
        }
    }
}
