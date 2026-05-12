package cpt204.groupproject.sort;

import cpt204.groupproject.model.LocationCandidate;

import java.util.Comparator;

/**
 * Required ranking rule for Task A: higher {@code priority_score} first;
 * ties broken by lexicographically smaller {@code location_id} first so results are deterministic.
 */
public final class CandidateOrdering {

    private CandidateOrdering() {
    }

    public static Comparator<LocationCandidate> comparator() {
        return Comparator.comparingInt(LocationCandidate::priorityScore)
                .reversed()
                .thenComparing(LocationCandidate::locationId);
    }
}
