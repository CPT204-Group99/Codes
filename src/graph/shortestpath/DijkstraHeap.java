package graph.shortestpath;

import graph.Edge;
import graph.WeightedEdge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

/** Dijkstra with a binary min-heap; same {@code cost}, {@code parent}, {@code T} naming as Graph codes. */
public final class DijkstraHeap {

    private DijkstraHeap() {
    }

    public static ShortestPathComputation compute(int sourceVertex, List<List<Edge>> neighbors) {
        double[] cost = new double[neighbors.size()];
        for (int i = 0; i < cost.length; i++) {
            cost[i] = Double.POSITIVE_INFINITY;
        }
        cost[sourceVertex] = 0;

        int[] parent = new int[neighbors.size()];
        Arrays.fill(parent, -1);
        parent[sourceVertex] = -1;

        boolean[] inT = new boolean[neighbors.size()];
        List<Integer> T = new ArrayList<>();

        PriorityQueue<HeapNode> pq = new PriorityQueue<>();
        pq.add(new HeapNode(cost[sourceVertex], sourceVertex));

        while (!pq.isEmpty()) {
            HeapNode node = pq.poll();
            int u = node.u;
            if (node.cost > cost[u]) {
                continue;
            }
            if (inT[u]) {
                continue;
            }
            inT[u] = true;
            T.add(u);

            for (Edge e : neighbors.get(u)) {
                if (!inT[e.v]
                        && cost[e.v] > cost[u] + ((WeightedEdge) e).weight) {
                    cost[e.v] = cost[u] + ((WeightedEdge) e).weight;
                    parent[e.v] = u;
                    pq.add(new HeapNode(cost[e.v], e.v));
                }
            }
        }

        return new ShortestPathComputation(sourceVertex, parent, T, cost);
    }

    private static final class HeapNode implements Comparable<HeapNode> {
        final double cost;
        final int u;

        HeapNode(double cost, int u) {
            this.cost = cost;
            this.u = u;
        }

        @Override
        public int compareTo(HeapNode o) {
            int c = Double.compare(cost, o.cost);
            if (c != 0) {
                return c;
            }
            return Integer.compare(u, o.u);
        }
    }
}
