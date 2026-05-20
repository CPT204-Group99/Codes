package graph.shortestpath;

import java.util.List;


public record ShortestPathComputation(int sourceVertex, int[] parent, List<Integer> T, double[] cost) {
    
}
