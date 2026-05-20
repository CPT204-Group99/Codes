package graph.shortestpath;

import graph.Edge;
import graph.WeightedEdge;

import java.util.ArrayList;
import java.util.List;


public final class DijkstraNormal {

    private DijkstraNormal() {
    }

    public static ShortestPathComputation compute(int sourceVertex, List<List<Edge>> neighbors) {
        double[] cost = new double[neighbors.size()];
        for (int i = 0; i < cost.length; i++) {
            cost[i] = Double.POSITIVE_INFINITY;
        }
        cost[sourceVertex] = 0;

   
        int[] parent = new int[neighbors.size()];
        parent[sourceVertex] = -1;

    
        List<Integer> T = new ArrayList<>();

        while (T.size() < neighbors.size()) {
            int u = -1;
            double currentMinCost = Double.POSITIVE_INFINITY;
            for (int i = 0; i < neighbors.size(); i++) {
                if (!T.contains(i) && cost[i] < currentMinCost) {
                    currentMinCost = cost[i];
                    u = i;
                }
            }

            if (u == -1) {
                break;
            } else {
                T.add(u);
            }

            for (Edge e : neighbors.get(u)) {
                if (!T.contains(e.v)
                        && cost[e.v] > cost[u] + ((WeightedEdge) e).weight) {
                    cost[e.v] = cost[u] + ((WeightedEdge) e).weight;
                    parent[e.v] = u;
                }
            }
        }

        return new ShortestPathComputation(sourceVertex, parent, T, cost);
    }
}
