package graph;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 从指定源点运行 Dijkstra 算法，得到单源最短路树（非负权）。
     *
     * @param sourceVertexIndex 源点在内部编号（与 {@link #getIndex(Object)} 一致）
     * @return 最短路树，可用 {@link ShortestPathTree#getCost(int)}、{@link UnweightedGraph.SearchTree#getPath(int)} 查询
     */
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

    /** 在尚未加入 T 的顶点中选 cost 最小的一个（教材中的“选最小 cost 的 u”）。 */
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
