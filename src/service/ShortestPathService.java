package service;

import graph.WeightedGraph;
import model.PathResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShortestPathService {

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
        List<String> stops = new ArrayList<>();
        stops.add(startId);
        stops.addAll(orderedWaypoints);
        stops.add(destinationId);

        List<String> fullPath = new ArrayList<>();
        double totalCost = 0;

        for (int i = 0; i < stops.size() - 1; i++) {
            PathSegment segment = findSegment(stops.get(i), stops.get(i + 1));
            totalCost += segment.cost;
            appendSegment(fullPath, segment.path, i > 0);
        }

        return new PathResult(caseName, startId, destinationId, orderedWaypoints, fullPath, totalCost);
    }

    private PathSegment findSegment(String startId, String destinationId) {
        int startIndex = graph.getIndex(startId);
        int destinationIndex = graph.getIndex(destinationId);

        if (startIndex < 0) {
            throw new IllegalArgumentException("Unknown start location: " + startId);
        }
        if (destinationIndex < 0) {
            throw new IllegalArgumentException("Unknown destination location: " + destinationId);
        }

        WeightedGraph<String>.ShortestPathTree tree = graph.dijkstraShortestPathTree(startIndex);
        double cost = tree.getCost(destinationIndex);
        if (Double.isInfinite(cost)) {
            throw new IllegalStateException("No path found from " + startId + " to " + destinationId);
        }

        List<String> backwardPath = tree.getPath(destinationIndex);
        List<String> forwardPath = new ArrayList<>(backwardPath);
        Collections.reverse(forwardPath);
        return new PathSegment(forwardPath, cost);
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

        private PathSegment(List<String> path, double cost) {
            this.path = path;
            this.cost = cost;
        }
    }
}
