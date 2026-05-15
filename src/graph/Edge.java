package graph;

public class Edge {

    public final int u;
    public final int v;

    public Edge(int u, int v) {
        this.u = u;
        this.v = v;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Edge)) {
            return false;
        }
        Edge other = (Edge) obj;
        return u == other.u && v == other.v;
    }

    @Override
    public int hashCode() {
        return 31 * u + v;
    }
}
