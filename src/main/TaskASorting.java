package main;

import algorithm.SortAlgorithm;
import service.DataLoader;
import model.Location;
import service.SortingService;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Task A entry: load A/B/C CSVs, run bubble / quick / merge via {@link SortingService}, print top 10.
 * After each dataset, the top 10 locations are stored in {@link #getSelectedTop10ByDataset()} for Task B.
 */
public final class TaskASorting {

    /**
     * Keys: "Dataset A", "Dataset B", "Dataset C". Values: top 10 {@link Location} (own list, not a subList view).
     * Filled when {@link #main} runs; use {@link #getSelectedTop10ByDataset()} from Task B code.
     */
    private static final Map<String, List<Location>> selectedTop10ByDataset = new LinkedHashMap<>();

    private TaskASorting() {
    }

    
    public static Map<String, List<Location>> getSelectedTop10ByDataset() {
        Map<String, List<Location>> copy = new LinkedHashMap<>();
        for (String key : selectedTop10ByDataset.keySet()) {
            copy.put(key, new ArrayList<>(selectedTop10ByDataset.get(key)));
        }
        return copy;
    }

    public static void main(String[] args) throws Exception {
        selectedTop10ByDataset.clear();

        File baseDir = new File(args.length > 0 ? args[0] : "Group Project Datasets");
        String[] files = {"candidates_A.csv", "candidates_B.csv", "candidates_C.csv"};

        SortingService sortingService = new SortingService();

        System.out.println("Dataset directory: " + baseDir.getAbsoluteFile().getCanonicalPath());
        System.out.println("Ranking: priority_score DESC, tie-break location_id ASC (Location.compareTo)");
        System.out.println();

        for (String file : files) {
            File csvFile = new File(baseDir, file);
            List<Location> list = DataLoader.load(csvFile);

            System.out.println("=== " + file + " (" + list.size() + " rows) ===");
            describeDataset(file);
            System.out.println();

            List<SortAlgorithm> algorithms = sortingService.allAlgorithms();
            for (int i = 0; i < algorithms.size(); i++) {
                SortAlgorithm alg = algorithms.get(i);
                List<Location> copy = sortingService.copyList(list);
                long elapsedNs = alg.sort(copy);

                if (!SortingService.isSorted(copy)) {
                    throw new IllegalStateException(alg.getName() + " failed sort check");
                }

                System.out.println(alg.getName());
                System.out.printf("  compareTo calls: %,d%n", alg.getComparisonCount());
                System.out.printf("  wall time: %.3f ms%n", elapsedNs / 1_000_000.0);
                System.out.println("  top 10 (rank, location_id, priority_score):");
                List<Location> top = SortingService.topN(copy, 10);
                for (int r = 0; r < top.size(); r++) {
                    Location c = top.get(r);
                    System.out.printf("    %2d  %s  %d%n",
                            r + 1, c.getLocationId(), c.getPriorityScore());
                }
                System.out.println();

                if (i == algorithms.size() - 1) {
                    String tag = file.substring("candidates_".length(), file.length() - ".csv".length());
                    String datasetKey = "Dataset " + tag;
                    selectedTop10ByDataset.put(datasetKey, new ArrayList<>(top));
                }
            }
            System.out.println();
        }
    }

    private static void describeDataset(String file) {
        String tag = file.substring("candidates_".length(), file.length() - ".csv".length());
        switch (tag) {
            case "A" -> System.out.println(
                    "Property: mostly pre-sorted by score descending (with small local inversions);");
            case "B" -> System.out.println(
                    "Property: pseudo-random order");
            case "C" -> System.out.println(
                    "Property: heavy ties on score; tie-break by location_id in compareTo.");
            default -> System.out.println("Property: (see brief for dataset " + tag + ").");
        }
    }
}
