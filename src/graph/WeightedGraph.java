package graph;

import graph.shortestpath.BellmanFord;
import graph.shortestpath.DijkstraNormal;
import graph.shortestpath.DijkstraHeap;
import graph.shortestpath.ShortestPathComputation;

import java.util.ArrayList;
import java.util.List;

public class WeightedGraph<V> extends UnweightedGraph<V> {

    public static final int DIJKSTRA_ARRAY = 0;
    public static final int BELLMAN_FORD = 1;
    public static final int DIJKSTRA_HEAP = 2;

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

    public ShortestPathTree shortestPathTree(int sourceVertex, int algorithmType) {
        ShortestPathComputation result;
        if (algorithmType == DIJKSTRA_ARRAY) {
            result = DijkstraNormal.compute(sourceVertex, neighbors);
        } else if (algorithmType == BELLMAN_FORD) {
            result = BellmanFord.compute(sourceVertex, neighbors);
        } else if (algorithmType == DIJKSTRA_HEAP) {
            result = DijkstraHeap.compute(sourceVertex, neighbors);
        } else {
            throw new IllegalArgumentException("Unknown algorithm type: " + algorithmType);
        }
        return toShortestPathTree(result);
    }

    public ShortestPathTree getShortestPath(int sourceVertex) {
        return shortestPathTree(sourceVertex, DIJKSTRA_ARRAY);
    }

    public ShortestPathTree dijkstraShortestPathTreeHeap(int sourceVertex) {
        return shortestPathTree(sourceVertex, DIJKSTRA_HEAP);
    }

    public ShortestPathTree bellmanFordShortestPathTree(int sourceVertex) {
        return shortestPathTree(sourceVertex, BELLMAN_FORD);
    }

    private ShortestPathTree toShortestPathTree(ShortestPathComputation result) {
        return new ShortestPathTree(result.sourceVertex(), result.parent(), result.T(), result.cost());
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
