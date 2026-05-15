package explore.dmsy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;

/**
 * Lemma 3.3 — data structure D (Initialize(M,B), Insert, BatchPrepend, Pull).
 * Semantics match the paper (Pull returns at most M smallest keys by value, with separator B_i);
 * implementation uses an explicit sorted multiset (correct for all n; not the paper's amortised time).
 */
public final class Lemma33DataStructureD {

    private final int m;
    private final double globalUpperB;
    private final NavigableSet<Entry> entries = new TreeSet<>(Comparator
            .comparingDouble((Entry e) -> e.value())
            .thenComparingInt(Entry::vertex));
    private final Map<Integer, Entry> byVertex = new HashMap<>();

    public Lemma33DataStructureD(int m, double globalUpperB) {
        if (m < 1) {
            throw new IllegalArgumentException("M >= 1");
        }
        this.m = m;
        this.globalUpperB = globalUpperB;
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    /** Smallest stored value, or +∞ if empty (for BatchPrepend precondition checks). */
    public double peekMinValue() {
        return entries.isEmpty() ? Double.POSITIVE_INFINITY : entries.first().value();
    }

    public void insert(int vertex, double value) {
        Entry old = byVertex.remove(vertex);
        if (old != null) {
            entries.remove(old);
        }
        Entry e = new Entry(vertex, value);
        byVertex.put(vertex, e);
        entries.add(e);
    }

    /**
     * Batch prepend: every value in {@code batch} is strictly smaller than any value currently stored
     * (Lemma 3.3 precondition); same key keeps smallest value.
     */
    public void batchPrepend(List<Entry> batch) {
        if (batch.isEmpty()) {
            return;
        }
        double minExisting = entries.isEmpty() ? globalUpperB : entries.first().value();
        for (Entry e : batch) {
            if (!(e.value() < minExisting - 1e-15)) {
                throw new IllegalStateException("BatchPrepend requires all values < current min");
            }
        }
        for (Entry e : batch) {
            insert(e.vertex(), e.value());
        }
    }

    /**
     * Pull: at most M keys with smallest values; B_i is strict upper bound for pulled set, lower bound
     * for remainder (or globalUpperB if D becomes empty).
     */
    public PullResult pull() {
        if (entries.isEmpty()) {
            return new PullResult(new ArrayList<>(), globalUpperB);
        }
        List<Entry> pulled = new ArrayList<>();
        int take = Math.min(m, entries.size());
        for (int i = 0; i < take; i++) {
            Entry e = entries.pollFirst();
            Objects.requireNonNull(e);
            byVertex.remove(e.vertex);
            pulled.add(e);
        }
        double bi = entries.isEmpty() ? globalUpperB : entries.first().value();
        return new PullResult(pulled, bi);
    }

    public record Entry(int vertex, double value) {
    }

    public record PullResult(List<Entry> pulledKeys, double separatorBi) {
        public List<Integer> verticesOnly() {
            return pulledKeys.stream().map(Entry::vertex).toList();
        }
    }
}
