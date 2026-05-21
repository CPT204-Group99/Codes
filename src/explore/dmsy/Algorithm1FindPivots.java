package explore.dmsy;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Algorithm 1 — FindPivots(B, S), Lemma 3.2, arXiv:2504.17033v2. */
public final class Algorithm1FindPivots {

    private static final double EPS = 1e-9;

    private Algorithm1FindPivots() {
    }

    public record Result(Set<Integer> pivotsP, Set<Integer> setW) {
    }

    public static Result run(DirectedGraph g,
                             PathEstimates est,
                             double upperBoundB,
                             Set<Integer> s,
                             int k) {
        if (s.isEmpty()) {
            throw new IllegalArgumentException("S must be non-empty");
        }
        if (k < 1) {
            throw new IllegalArgumentException("k must be >= 1");
        }
        double[] db = est.db();

        Set<Integer> w = new HashSet<>(s);
        Set<Integer> wPrev = new HashSet<>(s);

        for (int i = 1; i <= k; i++) {
            Set<Integer> wi = new HashSet<>();
            for (int u : wPrev) {
                for (DirectedGraph.Edge e : g.outgoing(u)) {
                    int v = e.to();
                    double cand = db[u] + e.weight();
                    est.relaxLeq(u, v, e.weight());
                    if (cand < upperBoundB - EPS) {
                        wi.add(v);
                    }
                }
            }
            w.addAll(wi);
            wPrev = wi;

            if (w.size() > (long) k * s.size()) {
                return new Result(new HashSet<>(s), w);
            }
        }

        List<int[]> tightEdges = new ArrayList<>();
        for (DirectedGraph.Edge e : g.allEdges()) {
            int u = e.from();
            int v = e.to();
            if (!w.contains(u) || !w.contains(v)) {
                continue;
            }
            if (Math.abs(db[v] - db[u] - e.weight()) <= EPS) {
                tightEdges.add(new int[]{u, v});
            }
        }

        Set<Integer> pivots = pivotsFromForest(g.n(), tightEdges, s, w, k);
        return new Result(pivots, w);
    }

    private static Set<Integer> pivotsFromForest(int n,
                                                 List<int[]> tightEdges,
                                                 Set<Integer> s,
                                                 Set<Integer> w,
                                                 int k) {
        Map<Integer, Integer> parent = new HashMap<>();
        for (int v : w) {
            int bestU = -1;
            for (int[] uv : tightEdges) {
                int u = uv[0];
                int vv = uv[1];
                if (vv != v || !w.contains(u)) {
                    continue;
                }
                if (bestU < 0 || u < bestU) {
                    bestU = u;
                }
            }
            if (bestU >= 0) {
                parent.put(v, bestU);
            }
        }

        List<List<Integer>> children = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            children.add(new ArrayList<>());
        }
        for (Map.Entry<Integer, Integer> e : parent.entrySet()) {
            children.get(e.getValue()).add(e.getKey());
        }

        Set<Integer> pivots = new HashSet<>();
        for (int u : s) {
            if (!w.contains(u)) {
                continue;
            }
            Integer p = parent.get(u);
            if (p != null && w.contains(p)) {
                continue;
            }
            if (subtreeSize(u, children) >= k) {
                pivots.add(u);
            }
        }
        return pivots;
    }

    private static int subtreeSize(int root, List<List<Integer>> children) {
        int c = 1;
        ArrayDeque<Integer> dq = new ArrayDeque<>();
        dq.add(root);
        while (!dq.isEmpty()) {
            int u = dq.removeFirst();
            for (int v : children.get(u)) {
                c++;
                dq.addLast(v);
            }
        }
        return c;
    }
}
