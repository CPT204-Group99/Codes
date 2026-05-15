package explore.dmsy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Top-level single-source shortest paths driver from Section 3 / Theorem 1.1:
 * one call BMSSP(l_top, ∞, {s}) with l_top = ⌈(log n)/t⌉ as in the paper (arXiv:2504.17033v2).
 * <p>
 * The paper assumes a constant-degree transformation on G; this driver runs on the given graph
 * as-is (see {@link DirectedGraph}).
 * <p>
 * Lemma 3.3 structure {@link Lemma33DataStructureD} matches the specified operations and ordering
 * semantics; it is not the paper's block-based implementation (asymptotic time differs).
 */
public final class DmsySingleSourceShortestPaths {

    private DmsySingleSourceShortestPaths() {
    }

    /**
     * Runs the paper's BMSSP root call and returns distance estimates (mutable shared with {@code est}).
     */
    public static PathEstimates compute(DirectedGraph g, int source) {
        int n = g.n();
        int k = PaperParams.k(n);
        int t = PaperParams.t(n);
        int lTop = PaperParams.lTop(n);

        PathEstimates est = new PathEstimates(n);
        est.setSource(source);

        Set<Integer> s0 = new HashSet<>();
        s0.add(source);

        Algorithm3Bmssp.run(g, est, lTop, PathEstimates.INF, s0, k, t);
        return est;
    }

    public static List<DirectedGraph.Edge> edgesFromTriple(int n, int[][] triples) {
        List<DirectedGraph.Edge> list = new ArrayList<>();
        for (int[] tr : triples) {
            list.add(new DirectedGraph.Edge(tr[0], tr[1], tr[2]));
        }
        return list;
    }
}
