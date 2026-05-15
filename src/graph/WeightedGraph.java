package graph;

import graph.shortestpath.BellmanFord;
import graph.shortestpath.DijkstraNormal;
import graph.shortestpath.DijkstraHeap;
import graph.shortestpath.ShortestPathComputation;

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

    public List<WeightedEdge> getAllWeightedEdges() {
        List<WeightedEdge> list = new ArrayList<>();
        for (List<Edge> adj : neighbors) {
            for (Edge e : adj) {
                list.add((WeightedEdge) e);
            }
        }
        return list;
    }

 
    public ShortestPathTree getShortestPath(int sourceVertex) {
        return toShortestPathTree(DijkstraNormal.compute(sourceVertex, neighbors));
    }

    public ShortestPathTree dijkstraShortestPathTreeHeap(int sourceVertex) {
        return toShortestPathTree(DijkstraHeap.compute(sourceVertex, neighbors));
    }

    public ShortestPathTree bellmanFordShortestPathTree(int sourceVertex) {
        return toShortestPathTree(BellmanFord.compute(sourceVertex, neighbors));
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
