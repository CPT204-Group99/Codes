package explore;

import explore.dmsy.DirectedGraph;
import explore.dmsy.DmsySingleSourceShortestPaths;
import explore.dmsy.PathEstimates;

import java.util.Arrays;

/**
 * Runs the arXiv:2504.17033v2 implementation under {@code explore.dmsy} (BMSSP + FindPivots + Lemma 3.3 D).
 */
public final class ExploreMain {

    private ExploreMain() {
    }

    public static void main(String[] args) {
        System.out.println("explore — Duan–Mao–Mao–Shu–Yin (arXiv:2504.17033v2) SSSP (dmsy package)");
        System.out.println();

        int[][] triples = {
                {0, 1, 1},
                {1, 2, 1},
                {0, 2, 5}
        };
        DirectedGraph g = new DirectedGraph(3, DmsySingleSourceShortestPaths.edgesFromTriple(3, triples));
        PathEstimates est = DmsySingleSourceShortestPaths.compute(g, 0);
        System.out.println("BMSSP root distances db = " + Arrays.toString(est.db()));
    }
}
