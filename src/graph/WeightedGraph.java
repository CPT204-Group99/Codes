package graph;

import java.util.ArrayList;
import java.util.List;

public class WeightedGraph<V> extends UnweightedGraph<V> {

    public WeightedGraph() {
    }

    public WeightedGraph(List<V> vertices, List<WeightedEdge> edges) {
        createWeightedGraph(vertices, edges);
    }

    private void createWeightedGraph(List<V> vertices, List<WeightedEdge> edges) {
        this.vertices = new ArrayList<>();
        this.neighbors = new ArrayList<>();

        for (V vertex : vertices) {
            addVertex(vertex);
        }
        for (WeightedEdge edge : edges) {
            neighbors.get(edge.u).add(edge);
        }
    }

    public double getWeight(int u, int v) throws Exception {
        for (Edge edge : neighbors.get(u)) {
            if (edge.v == v) {
                return ((WeightedEdge) edge).weight;
            }
        }
        throw new Exception("Edge does not exist");
    }

    public void printWeightedEdges() {
        for (int i = 0; i < getSize(); i++) {
            System.out.print(getVertex(i) + " (" + i + "): ");
            for (Edge edge : neighbors.get(i)) {
                WeightedEdge weightedEdge = (WeightedEdge) edge;
                System.out.print("(" + edge.u + ", " + edge.v + ", " + weightedEdge.weight + ") ");
            }
            System.out.println();
        }
    }

    public boolean addEdge(int u, int v, double weight) {
        return addEdge(new WeightedEdge(u, v, weight));
    }

    public ShortestPathTree getShortestPath(int sourceVertex) {
        double[] cost = new double[getSize()];
        for (int i = 0; i < cost.length; i++) {
            cost[i] = Double.POSITIVE_INFINITY;
        }
        cost[sourceVertex] = 0;

        int[] parent = new int[getSize()];
        for (int i = 0; i < parent.length; i++) {
            parent[i] = -1;
        }

        List<Integer> settled = new ArrayList<>();

        while (settled.size() < getSize()) {
            int u = -1;
            double currentMinCost = Double.POSITIVE_INFINITY;
            for (int i = 0; i < getSize(); i++) {
                if (!settled.contains(i) && cost[i] < currentMinCost) {
                    currentMinCost = cost[i];
                    u = i;
                }
            }

            if (u == -1) {
                break;
            }
            settled.add(u);

            for (Edge edge : neighbors.get(u)) {
                WeightedEdge weightedEdge = (WeightedEdge) edge;
                if (!settled.contains(edge.v) && cost[edge.v] > cost[u] + weightedEdge.weight) {
                    cost[edge.v] = cost[u] + weightedEdge.weight;
                    parent[edge.v] = u;
                }
            }
        }

        return new ShortestPathTree(sourceVertex, parent, settled, cost);
    }

    public class ShortestPathTree extends SearchTree {

        private final double[] cost;

        public ShortestPathTree(int source, int[] parent, List<Integer> searchOrder, double[] cost) {
            super(source, parent, searchOrder);
            this.cost = cost;
        }

        public double getCost(int vertexIndex) {
            return cost[vertexIndex];
        }
    }
}
