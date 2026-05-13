package algorithm;

import model.Location;

import java.util.List;

public interface SortAlgorithm {
    long sort(List<Location> locations);

    String getName();

    long getComparisonCount();
}
