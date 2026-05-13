package graph;

public class WeightedEdge extends Edge implements Comparable<WeightedEdge> {

    public final double weight;

    public WeightedEdge(int u, int v, double weight) {
        super(u, v);
        this.weight = weight;
    }

    @Override
    public int compareTo(WeightedEdge other) {
        return Double.compare(weight, other.weight);
    }
}
