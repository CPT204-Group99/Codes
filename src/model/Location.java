package model;

/**
 * One candidate row: location id and priority score for Task A ranking.
 */
public class Location implements Comparable<Location> {

    private final String locationId;
    private final int priorityScore;

    public Location(String locationId, int priorityScore) {
        if (locationId == null) {
            throw new NullPointerException("locationId");
        }
        this.locationId = locationId;
        this.priorityScore = priorityScore;
    }

    public String getLocationId() {
        return locationId;
    }

    public int getPriorityScore() {
        return priorityScore;
    }

    /**
     * Higher priority score first; tie-break by smaller location id (lexicographic).
     */
    @Override
    public int compareTo(Location other) {
        if (this.priorityScore != other.priorityScore) {
            return Integer.compare(other.priorityScore, this.priorityScore);
        }
        return this.locationId.compareTo(other.locationId);
    }

    @Override
    public String toString() {
        return locationId + "," + priorityScore;
    }
}
