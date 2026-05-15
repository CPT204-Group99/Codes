package graph.shortestpath;

import graph.Edge;
import graph.WeightedEdge;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class BellmanFord {

    private BellmanFord() {
    }

    public static ShortestPathComputation compute(int sourceVertex, List<List<Edge>> neighbors) {
        double[] cost = new double[neighbors.size()];
        Arrays.fill(cost, Double.POSITIVE_INFINITY);
        cost[sourceVertex] = 0;

        int[] parent = new int[neighbors.size()];
        Arrays.fill(parent, -1);
        parent[sourceVertex] = -1;

        for (int pass = 0; pass < neighbors.size() - 1; pass++) {
            relaxAllEdges(cost, parent, neighbors);
        }
        if (hasProfitableRelaxation(cost, neighbors)) {
            throw new IllegalStateException(
                    "Negative-weight cycle reachable from source (Bellman-Ford)");
        }

        return new ShortestPathComputation(sourceVertex, parent, Collections.emptyList(), cost);
    }

    private static void relaxAllEdges(double[] cost, int[] parent, List<List<Edge>> neighbors) {
        for (int u = 0; u < neighbors.size(); u++) {
            if (!Double.isFinite(cost[u])) {
                continue;
            }
            for (Edge e : neighbors.get(u)) {
                if (cost[e.v] > cost[u] + ((WeightedEdge) e).weight) {
                    cost[e.v] = cost[u] + ((WeightedEdge) e).weight;
                    parent[e.v] = u;
                }
            }
        }
    }

    private static boolean hasProfitableRelaxation(double[] cost, List<List<Edge>> neighbors) {
        for (int u = 0; u < neighbors.size(); u++) {
            if (!Double.isFinite(cost[u])) {
                continue;
            }
            for (Edge e : neighbors.get(u)) {
                if (cost[e.v] > cost[u] + ((WeightedEdge) e).weight) {
                    return true;
                }
            }
        }
        return false;
    }
}
