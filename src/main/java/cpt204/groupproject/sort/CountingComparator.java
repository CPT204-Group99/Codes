package cpt204.groupproject.sort;

import java.util.Comparator;

/**
 * Wraps a comparator to count how many times {@link #compare} is invoked (proxy for key comparisons).
 */
public final class CountingComparator<T> implements Comparator<T> {

    private final Comparator<? super T> delegate;
    private long comparisons;

    public CountingComparator(Comparator<? super T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int compare(T a, T b) {
        comparisons++;
        return delegate.compare(a, b);
    }

    public long comparisons() {
        return comparisons;
    }

    public void reset() {
        comparisons = 0;
    }
}
