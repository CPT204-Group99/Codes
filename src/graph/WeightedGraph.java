package graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class WeightedGraph<V> extends UnweightedGraph<V> {

    public WeightedGraph(List<V> vertices, List<WeightedEdge> edges) {
        createWeightedGraph(vertices, edges);
    }

    private void createWeightedGraph(List<V> vertices, List<WeightedEdge> edges) {
        this.vertices = vertices;
        for (int i = 0; i < vertices.size(); i++) {
            neighbors.add(new ArrayList<>());
        }
        for (WeightedEdge edge : edges) {
            neighbors.get(edge.u).add(edge);
        }
    }

    public List<WeightedEdge> getAllWeightedEdges() {
        List<WeightedEdge> list = new ArrayList<>();
        for (List<Edge> adj : neighbors) {
            for (Edge e : adj) {
                list.add((WeightedEdge) e);
            }
        }
        return list;
    }

    public ShortestPathTree dijkstraShortestPathTree(int sourceVertexIndex) {
        double[] cost = new double[getSize()];
        for (int i = 0; i < cost.length; i++) {
            cost[i] = Double.POSITIVE_INFINITY;
        }
        cost[sourceVertexIndex] = 0;

        int[] parent = new int[getSize()];
        parent[sourceVertexIndex] = -1;

        List<Integer> settledVertices = new ArrayList<>();

        while (settledVertices.size() < getSize()) {
            int u = pickUnsettledWithMinCost(cost, settledVertices);
            if (u == -1) {
                break;
            }
            settledVertices.add(u);
            relaxNeighbors(u, cost, parent, settledVertices);
        }

        return new ShortestPathTree(sourceVertexIndex, parent, settledVertices, cost);
    }

    
    /**
     * Dijkstra + 二叉堆：每次从堆取出「距离估计最小」的顶点做松弛。
     * 堆里可能留有旧距离；弹出若已大于当前 dist[u]，说明是过期条目，跳过即可。
     */
    public ShortestPathTree dijkstraShortestPathTreeHeap(int sourceVertexIndex) {
        int n = getSize();
        double[] cost = new double[n];
        Arrays.fill(cost, Double.POSITIVE_INFINITY);
        cost[sourceVertexIndex] = 0;

        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        boolean[] done = new boolean[n];
        List<Integer> searchOrder = new ArrayList<>();

        PriorityQueue<DijkstraHeapNode> pq = new PriorityQueue<>();
        pq.add(new DijkstraHeapNode(0.0, sourceVertexIndex));

        while (!pq.isEmpty()) {
            DijkstraHeapNode node = pq.poll();
            int u = node.vertex;
            double du = node.dist;
            if (du > cost[u]) {
                continue;
            }
            if (done[u]) {
                continue;
            }
            done[u] = true;
            searchOrder.add(u);

            for (Edge e : neighbors.get(u)) {
                double w = ((WeightedEdge) e).weight;
                int v = e.v;
                double next = cost[u] + w;
                if (next < cost[v]) {
                    cost[v] = next;
                    parent[v] = u;
                    pq.add(new DijkstraHeapNode(next, v));
                }
            }
        }

        return new ShortestPathTree(sourceVertexIndex, parent, searchOrder, cost);
    }


    public ShortestPathTree bellmanFordShortestPathTree(int sourceVertexIndex) {
        int n = getSize();
        double[] cost = new double[n];
        Arrays.fill(cost, Double.POSITIVE_INFINITY);
        cost[sourceVertexIndex] = 0;

        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        for (int round = 0; round < n - 1; round++) {
            relaxAllEdgesBellmanFord(cost, parent);
        }
        if (bellmanFordHasProfitableRelaxation(cost)) {
            throw new IllegalStateException(
                    "Negative-weight cycle reachable from source (Bellman-Ford)");
        }

        return new ShortestPathTree(sourceVertexIndex, parent, Collections.emptyList(), cost);
    }

    private void relaxAllEdgesBellmanFord(double[] cost, int[] parent) {
        for (int u = 0; u < getSize(); u++) {
            if (!Double.isFinite(cost[u])) {
                continue;
            }
            for (Edge e : neighbors.get(u)) {
                double w = ((WeightedEdge) e).weight;
                int v = e.v;
                if (cost[v] > cost[u] + w) {
                    cost[v] = cost[u] + w;
                    parent[v] = u;
                }
            }
        }
    }

    private boolean bellmanFordHasProfitableRelaxation(double[] cost) {
        for (int u = 0; u < getSize(); u++) {
            if (!Double.isFinite(cost[u])) {
                continue;
            }
            for (Edge e : neighbors.get(u)) {
                double w = ((WeightedEdge) e).weight;
                int v = e.v;
                if (cost[v] > cost[u] + w) {
                    return true;
                }
            }
        }
        return false;
    }

    private int pickUnsettledWithMinCost(double[] cost, List<Integer> settledVertices) {
        int best = -1;
        double bestCost = Double.POSITIVE_INFINITY;
        for (int i = 0; i < getSize(); i++) {
            if (!settledVertices.contains(i) && cost[i] < bestCost) {
                bestCost = cost[i];
                best = i;
            }
        }
        return best;
    }

    /** 对 u 的所有邻边做 Dijkstra 松弛。 */
    private void relaxNeighbors(int u, double[] cost, int[] parent, List<Integer> settledVertices) {
        for (Edge e : neighbors.get(u)) {
            if (!settledVertices.contains(e.v) && cost[e.v] > cost[u] + ((WeightedEdge) e).weight) {
                cost[e.v] = cost[u] + ((WeightedEdge) e).weight;
                parent[e.v] = u;
            }
        }
    }

    /** 堆元素：顶点编号 + 入堆时的距离（供优先队列排序）。 */
    private static final class DijkstraHeapNode implements Comparable<DijkstraHeapNode> {
        final double dist;
        final int vertex;

        DijkstraHeapNode(double dist, int vertex) {
            this.dist = dist;
            this.vertex = vertex;
        }

        @Override
        public int compareTo(DijkstraHeapNode o) {
            int c = Double.compare(dist, o.dist);
            if (c != 0) {
                return c;
            }
            return Integer.compare(vertex, o.vertex);
        }
    }

    public class ShortestPathTree extends SearchTree {
        private final double[] cost;

        public ShortestPathTree(int source, int[] parent, List<Integer> searchOrder, double[] cost) {
            super(source, parent, searchOrder);
            this.cost = cost;
        }

        public double getCost(int v) {
            return cost[v];
        }
    }
}
