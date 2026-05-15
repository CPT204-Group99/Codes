package explore.dmsy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Algorithm 3 — BMSSP(l, B, S), arXiv:2504.17033v2 (bounded multi-source shortest paths).
 */
public final class Algorithm3Bmssp {

    private static final double EPS = 1e-9;

    private Algorithm3Bmssp() {
    }

    public record BmsspResult(double boundaryBprime, Set<Integer> vertexSetU, boolean partialStop) {
    }

    public static BmsspResult run(DirectedGraph g,
                                  PathEstimates est,
                                  int l,
                                  double upperBoundB,
                                  Set<Integer> s,
                                  int k,
                                  int t) {
        double[] db = est.db();

        if (l == 0) {
            Algorithm2BaseCase.Result bc = Algorithm2BaseCase.run(g, est, upperBoundB, s, k);
            return new BmsspResult(bc.boundaryBp(), bc.vertexSetU(), false);
        }

        Algorithm1FindPivots.Result piv = Algorithm1FindPivots.run(g, est, upperBoundB, s, k);
        Set<Integer> p = piv.pivotsP();
        Set<Integer> w = piv.setW();

        int bigM = PaperParams.bigM(l, t);
        Lemma33DataStructureD d = new Lemma33DataStructureD(bigM, upperBoundB);
        for (int x : p) {
            d.insert(x, db[x]);
        }

        Set<Integer> uAccum = new HashSet<>();
        long threshold = PaperParams.thresholdKTimes2PowLt(k, l, t);

        double minBprimeI = upperBoundB;
        double lastBprimeI = upperBoundB;
        boolean partial = false;

        while (uAccum.size() < threshold && !d.isEmpty()) {
            Lemma33DataStructureD.PullResult pull = d.pull();
            List<Integer> si = pull.verticesOnly();
            double bi = pull.separatorBi();

            if (si.isEmpty()) {
                break;
            }

            Set<Integer> siSet = new HashSet<>(si);
            BmsspResult child = run(g, est, l - 1, bi, siSet, k, t);
            double bPrimeI = child.boundaryBprime();
            lastBprimeI = bPrimeI;
            minBprimeI = Math.min(minBprimeI, bPrimeI);
            uAccum.addAll(child.vertexSetU());

            if (uAccum.size() > threshold) {
                partial = true;
                break;
            }

            List<Lemma33DataStructureD.Entry> batchK = new ArrayList<>();
            for (int uu : child.vertexSetU()) {
                for (DirectedGraph.Edge e : g.outgoing(uu)) {
                    int v = e.to();
                    if (!est.relaxLeq(uu, v, e.weight())) {
                        continue;
                    }
                    double val = db[v];
                    if (val >= bi - EPS && val < upperBoundB - EPS) {
                        d.insert(v, val);
                    } else if (val >= bPrimeI - EPS && val < bi - EPS) {
                        batchK.add(new Lemma33DataStructureD.Entry(v, val));
                    }
                }
            }

            List<Lemma33DataStructureD.Entry> prependSi = new ArrayList<>();
            for (int x : si) {
                if (db[x] >= bPrimeI - EPS && db[x] < bi - EPS) {
                    prependSi.add(new Lemma33DataStructureD.Entry(x, db[x]));
                }
            }
            List<Lemma33DataStructureD.Entry> batchAll = new ArrayList<>(batchK);
            batchAll.addAll(prependSi);
            batchAll.sort(Comparator.comparingDouble(Lemma33DataStructureD.Entry::value)
                    .thenComparingInt(Lemma33DataStructureD.Entry::vertex));
            applyBatchPrependOrInsert(d, batchAll);

            if (d.isEmpty()) {
                double bRet = Math.min(upperBoundB, minBprimeI);
                Set<Integer> uFinal = mergeWFilter(w, db, uAccum, bRet);
                return new BmsspResult(bRet, uFinal, false);
            }
        }

        if (partial) {
            Set<Integer> uFinal = mergeWFilter(w, db, uAccum, lastBprimeI);
            return new BmsspResult(lastBprimeI, uFinal, true);
        }

        double bRet = Math.min(upperBoundB, minBprimeI);
        Set<Integer> uFinal = mergeWFilter(w, db, uAccum, bRet);
        return new BmsspResult(bRet, uFinal, false);
    }

    private static void applyBatchPrependOrInsert(Lemma33DataStructureD d, List<Lemma33DataStructureD.Entry> batchAll) {
        if (batchAll.isEmpty()) {
            return;
        }
        double minExisting = d.isEmpty() ? Double.POSITIVE_INFINITY : d.peekMinValue();
        double maxBatch = batchAll.stream().mapToDouble(Lemma33DataStructureD.Entry::value).max().orElseThrow();
        if (maxBatch < minExisting - EPS) {
            d.batchPrepend(batchAll);
        } else {
            for (Lemma33DataStructureD.Entry e : batchAll) {
                d.insert(e.vertex(), e.value());
            }
        }
    }

    private static Set<Integer> mergeWFilter(Set<Integer> w, double[] db, Set<Integer> uAccum, double bRet) {
        Set<Integer> uFinal = new HashSet<>(uAccum);
        for (int x : w) {
            if (db[x] < bRet - EPS) {
                uFinal.add(x);
            }
        }
        return uFinal;
    }
}
