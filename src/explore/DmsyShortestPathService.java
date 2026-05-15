package explore;

import explore.dmsy.DirectedGraph;
import explore.dmsy.DmsySingleSourceShortestPaths;
import explore.dmsy.PathEstimates;
import graph.WeightedEdge;
import graph.WeightedGraph;
import model.PathResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Explore-only shortest-path queries via Duan et al. BMSSP (arXiv:2504.17033v2).
 */
public class DmsyShortestPathService {

    private final WeightedGraph<String> graph;
    private final DirectedGraph directed;

    public DmsyShortestPathService(WeightedGraph<String> graph) {
        this.graph = graph;
        this.directed = toDirectedGraph(graph);
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
        long totalAlgorithmNanos = 0;

        for (int i = 0; i < stops.size() - 1; i++) {
            PathSegment segment = segment(stops.get(i), stops.get(i + 1));
            totalCost += segment.cost;
            totalAlgorithmNanos += segment.algorithmElapsedNanos;
            appendSegment(fullPath, segment.path, i > 0);
        }

        return new PathResult(caseName, startId, destinationId, orderedWaypoints, fullPath, totalCost,
                totalAlgorithmNanos);
    }

    private PathSegment segment(String startId, String destinationId) {
        int startIndex = graph.getIndex(startId);
        int destinationIndex = graph.getIndex(destinationId);

        if (startIndex < 0) {
            throw new IllegalArgumentException("Unknown start location: " + startId);
        }
        if (destinationIndex < 0) {
            throw new IllegalArgumentException("Unknown destination location: " + destinationId);
        }

        long t0 = System.nanoTime();
        PathEstimates est = DmsySingleSourceShortestPaths.compute(directed, startIndex);
        long algorithmElapsedNanos = System.nanoTime() - t0;

        double cost = est.get(destinationIndex);
        if (Double.isInfinite(cost)) {
            throw new IllegalStateException("No path found from " + startId + " to " + destinationId);
        }

        List<String> path = reconstructPath(est, startIndex, destinationIndex);
        return new PathSegment(path, cost, algorithmElapsedNanos);
    }

    private List<String> reconstructPath(PathEstimates est, int sourceIndex, int destinationIndex) {
        if (sourceIndex == destinationIndex) {
            return List.of(graph.getVertex(sourceIndex));
        }

        int[] pred = est.pred();
        List<Integer> backward = new ArrayList<>();
        int cur = destinationIndex;
        while (cur != -1) {
            backward.add(cur);
            if (cur == sourceIndex) {
                break;
            }
            cur = pred[cur];
        }
        if (backward.isEmpty() || backward.get(backward.size() - 1) != sourceIndex) {
            throw new IllegalStateException("No path tree from " + sourceIndex + " to " + destinationIndex);
        }

        List<String> forward = new ArrayList<>(backward.size());
        for (int i = backward.size() - 1; i >= 0; i--) {
            forward.add(graph.getVertex(backward.get(i)));
        }
        return forward;
    }

    private static DirectedGraph toDirectedGraph(WeightedGraph<String> graph) {
        List<DirectedGraph.Edge> edges = new ArrayList<>();
        for (WeightedEdge e : graph.getAllWeightedEdges()) {
            edges.add(new DirectedGraph.Edge(e.u, e.v, e.weight));
        }
        return new DirectedGraph(graph.getSize(), edges);
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
