package graph.shortestpath;

import java.util.List;

public final class ShortestPathComputation {

    private final int sourceVertex;
    private final int[] parent;
    private final List<Integer> T;
    private final double[] cost;

    public ShortestPathComputation(int sourceVertex, int[] parent, List<Integer> T, double[] cost) {
        this.sourceVertex = sourceVertex;
        this.parent = parent;
        this.T = T;
        this.cost = cost;
    }

    public int sourceVertex() {
        return sourceVertex;
    }

    public int[] parent() {
        return parent;
    }

    public List<Integer> T() {
        return T;
    }

    public double[] cost() {
        return cost;
    }
}
