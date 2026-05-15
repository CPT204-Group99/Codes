package explore.dmsy;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Algorithm 2 — BaseCase(B, S) for BMSSP when l = 0 (singleton S = {x}), arXiv:2504.17033v2.
 */
public final class Algorithm2BaseCase {

    private static final double EPS = 1e-9;

    private Algorithm2BaseCase() {
    }

    public record Result(double boundaryBp, Set<Integer> vertexSetU) {
    }

    public static Result run(DirectedGraph g,
                             PathEstimates est,
                             double upperBoundB,
                             Set<Integer> s,
                             int k) {
        if (s.size() != 1) {
            throw new IllegalArgumentException("Base case requires |S| = 1");
        }
        int x = s.iterator().next();
        double[] db = est.db();

        Set<Integer> u0 = new HashSet<>();

        NavigableSet<Lemma33DataStructureD.Entry> heap = new TreeSet<>(Comparator
                .comparingDouble((Lemma33DataStructureD.Entry e) -> e.value())
                .thenComparingInt(Lemma33DataStructureD.Entry::vertex));
        Map<Integer, Lemma33DataStructureD.Entry> inHeap = new HashMap<>();

        java.util.function.IntConsumer pushVertex = (v) -> {
            Lemma33DataStructureD.Entry old = inHeap.remove(v);
            if (old != null) {
                heap.remove(old);
            }
            if (db[v] >= PathEstimates.INF / 2) {
                return;
            }
            Lemma33DataStructureD.Entry ne = new Lemma33DataStructureD.Entry(v, db[v]);
            inHeap.put(v, ne);
            heap.add(ne);
        };

        pushVertex.accept(x);

        boolean[] extracted = new boolean[g.n()];

        while (!heap.isEmpty() && u0.size() < k + 1) {
            Lemma33DataStructureD.Entry cur = heap.pollFirst();
            inHeap.remove(cur.vertex());
            if (Math.abs(cur.value() - db[cur.vertex()]) > EPS) {
                continue;
            }
            int u = cur.vertex();
            if (extracted[u]) {
                continue;
            }
            extracted[u] = true;
            u0.add(u);

            for (DirectedGraph.Edge e : g.outgoing(u)) {
                int v = e.to();
                double cand = db[u] + e.weight();
                if (cand > upperBoundB - EPS) {
                    continue;
                }
                if (!est.relaxLeq(u, v, e.weight())) {
                    continue;
                }
                pushVertex.accept(v);
            }
        }

        if (u0.size() <= k) {
            return new Result(upperBoundB, new HashSet<>(u0));
        }
        double maxVal = Double.NEGATIVE_INFINITY;
        for (int v : u0) {
            maxVal = Math.max(maxVal, db[v]);
        }
        double bp = maxVal;
        Set<Integer> u = new HashSet<>();
        for (int v : u0) {
            if (db[v] < bp - EPS) {
                u.add(v);
            }
        }
        return new Result(bp, u);
    }
}
