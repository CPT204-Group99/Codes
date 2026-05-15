package explore.dmsy;

/**
 * Parameters k, t and top recursion depth from Section 3 of arXiv:2504.17033v2:
 * k = ⌊log^{1/3} n⌋, t = ⌊log^{2/3} n⌋ (log base 2 as is standard in algorithm engineering).
 */
public final class PaperParams {

    private PaperParams() {
    }

    public static int k(int n) {
        double lg = log2(Math.max(2, n));
        return Math.max(1, (int) Math.floor(Math.cbrt(lg)));
    }

    public static int t(int n) {
        double lg = log2(Math.max(2, n));
        return Math.max(1, (int) Math.floor(Math.pow(lg, 2.0 / 3.0)));
    }

    /** Top level l = ⌈(log n)/t⌉ in the paper's BMSSP root call. */
    public static int lTop(int n) {
        double lg = log2(Math.max(2, n));
        int tt = t(n);
        return (int) Math.ceil(lg / tt);
    }

    /** M = 2^{(l-1)t} from Algorithm 3 line 5 (for l ≥ 1). */
    public static int bigM(int l, int t) {
        if (l <= 0) {
            return 1;
        }
        return pow2Bounded((l - 1) * t);
    }

    /** Threshold k · 2^{l t} appearing in Algorithm 3 (while / partial test in arXiv v2). */
    public static long thresholdKTimes2PowLt(int k, int l, int tt) {
        long pow = pow2Bounded(l * tt);
        long prod = (long) k * pow;
        if (prod < 0 || prod / pow != k) {
            return Long.MAX_VALUE / 4;
        }
        return prod;
    }

    private static double log2(double x) {
        return Math.log(x) / Math.log(2.0);
    }

    private static int pow2Bounded(int exp) {
        if (exp <= 0) {
            return 1;
        }
        if (exp >= 62) {
            return Integer.MAX_VALUE / 2;
        }
        return 1 << exp;
    }
}
