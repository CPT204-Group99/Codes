package service;

import graph.WeightedGraph;
import model.PathResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShortestPathService {

    private static final int ALGO_DIJKSTRA_ARRAY = 0;
    private static final int ALGO_BELLMAN_FORD = 1;
    private static final int ALGO_DIJKSTRA_HEAP = 2;

    private final WeightedGraph<String> graph;

    public ShortestPathService(WeightedGraph<String> graph) {
        this.graph = graph;
    }

    public PathResult findShortestPath(String caseName, String startId, String destinationId) {
        return findShortestPathVia(caseName, startId, destinationId, Collections.emptyList());
    }

    public PathResult findShortestPathVia(String caseName,
                                          String startId,
                                          String destinationId,
                                          List<String> orderedWaypoints) {
        return buildPathResult(caseName, startId, destinationId, orderedWaypoints, ALGO_DIJKSTRA_ARRAY);
    }

    public PathResult findShortestPathBellmanFord(String caseName, String startId, String destinationId) {
        return findShortestPathViaBellmanFord(caseName, startId, destinationId, Collections.emptyList());
    }

    public PathResult findShortestPathViaBellmanFord(String caseName,
                                                     String startId,
                                                     String destinationId,
                                                     List<String> orderedWaypoints) {
        return buildPathResult(caseName, startId, destinationId, orderedWaypoints, ALGO_BELLMAN_FORD);
    }

    public PathResult findShortestPathDijkstraHeap(String caseName, String startId, String destinationId) {
        return findShortestPathViaDijkstraHeap(caseName, startId, destinationId, Collections.emptyList());
    }

    public PathResult findShortestPathViaDijkstraHeap(String caseName,
                                                    String startId,
                                                    String destinationId,
                                                    List<String> orderedWaypoints) {
        return buildPathResult(caseName, startId, destinationId, orderedWaypoints, ALGO_DIJKSTRA_HEAP);
    }

    private PathResult buildPathResult(String caseName,
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
        WeightedGraph<String>.ShortestPathTree tree;
        if (algorithmType == ALGO_DIJKSTRA_ARRAY) {
            tree = graph.getShortestPath(startIndex);
        } else if (algorithmType == ALGO_BELLMAN_FORD) {
            tree = graph.bellmanFordShortestPathTree(startIndex);
        } else if (algorithmType == ALGO_DIJKSTRA_HEAP) {
            tree = graph.dijkstraShortestPathTreeHeap(startIndex);
        } else {
            throw new IllegalArgumentException("Unknown algorithm type: " + algorithmType);
        }
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
