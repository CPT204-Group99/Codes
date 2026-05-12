package cpt204.groupproject.model;

import java.util.Objects;

/**
 * One row from candidates_*.csv: a location with a priority score for ranking.
 */
public final class LocationCandidate {

    private final String locationId;
    private final int priorityScore;

    public LocationCandidate(String locationId, int priorityScore) {
        this.locationId = Objects.requireNonNull(locationId, "locationId");
        this.priorityScore = priorityScore;
    }

    public String locationId() {
        return locationId;
    }

    public int priorityScore() {
        return priorityScore;
    }

    @Override
    public String toString() {
        return locationId + "," + priorityScore;
    }
}
