package explore.dmsy;

import java.util.Arrays;

/**
 * Global distance estimates {@code db[·]} (paper notation \u0302d) with predecessor / hop information
 * for Assumption 2.1 style tie-breaking (lexicographic path tuples when lengths tie).
 */
public final class PathEstimates {

    public static final double INF = Double.POSITIVE_INFINITY;
    private static final double EPS = 1e-12;

    private final int n;
    private final double[] db;
    private final int[] pred;
    private final int[] hops;

    public PathEstimates(int n) {
        this.n = n;
        this.db = new double[n];
        this.pred = new int[n];
        this.hops = new int[n];
        Arrays.fill(db, INF);
        Arrays.fill(pred, -1);
        Arrays.fill(hops, 0);
    }

    public void setSource(int s) {
        Arrays.fill(db, INF);
        Arrays.fill(pred, -1);
        Arrays.fill(hops, 0);
        db[s] = 0.0;
        pred[s] = -1;
        hops[s] = 0;
    }

    public double[] db() {
        return db;
    }

    public int[] pred() {
        return pred;
    }

    public int[] hops() {
        return hops;
    }

    /**
     * Relax (u,v) when \u0302d[u]+w \u2264 \u0302d[v] in the paper's sense (line 7–8 Algorithm 1; line 15 Algorithm 3).
     * On exact length ties, apply Assumption 2.1 style rule (compare hop count then predecessor vertex id).
     */
    public boolean relaxLeq(int u, int v, double w) {
        if (db[u] >= INF / 2) {
            return false;
        }
        double cand = db[u] + w;
        if (cand < db[v] - EPS) {
            assign(v, cand, u);
            return true;
        }
        if (cand > db[v] + EPS) {
            return false;
        }
        int newHops = hops[u] + 1;
        if (newHops < hops[v]) {
            assign(v, cand, u);
            return true;
        }
        if (newHops > hops[v]) {
            return false;
        }
        int oldPred = pred[v];
        if (oldPred < 0 || u < oldPred) {
            assign(v, cand, u);
            return true;
        }
        return false;
    }

    private void assign(int v, double val, int p) {
        db[v] = val;
        pred[v] = p;
        hops[v] = p < 0 ? 0 : hops[p] + 1;
    }

    public double get(int v) {
        return db[v];
    }
}
