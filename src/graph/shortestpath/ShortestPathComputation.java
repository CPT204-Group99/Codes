package graph.shortestpath;

import java.util.List;

/**
 * Result of a single-source shortest-path run, passed to {@link graph.WeightedGraph.ShortestPathTree}.
 * {@code parent} and {@code cost} match Graph codes; {@code T} is the textbook search-order list.
 */
public record ShortestPathComputation(
        int sourceVertex,
        int[] parent,
        List<Integer> T,
        double[] cost) {
}
