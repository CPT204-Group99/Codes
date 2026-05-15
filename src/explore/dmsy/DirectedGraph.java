package explore.dmsy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Directed graph with non-negative real weights (comparison–addition style at double precision). */
public final class DirectedGraph {

    private final int n;
    private final List<Edge>[] out;

    @SuppressWarnings("unchecked")
    public DirectedGraph(int n, List<Edge> edges) {
        if (n < 0) {
            throw new IllegalArgumentException("n < 0");
        }
        this.n = n;
        this.out = new List[n];
        for (int i = 0; i < n; i++) {
            this.out[i] = new ArrayList<>();
        }
        for (Edge e : edges) {
            if (e.from < 0 || e.from >= n || e.to < 0 || e.to >= n) {
                throw new IllegalArgumentException("edge out of range: " + e);
            }
            if (e.weight < 0) {
                throw new IllegalArgumentException("negative weight: " + e);
            }
            out[e.from].add(e);
        }
    }

    public int n() {
        return n;
    }

    public List<Edge> outgoing(int u) {
        return Collections.unmodifiableList(out[u]);
    }

    public List<Edge> allEdges() {
        List<Edge> list = new ArrayList<>();
        for (int u = 0; u < n; u++) {
            list.addAll(out[u]);
        }
        return list;
    }

    public record Edge(int from, int to, double weight) {
    }
}
