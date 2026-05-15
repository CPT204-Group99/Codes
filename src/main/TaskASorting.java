package main;

import sortingalgorithms.SortAlgorithm;
import dataloader.DataLoader;
import model.Location;
import service.SortingService;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Task A: load A/B/C CSVs, run bubble / quick / merge via {@link SortingService}, print top 10 when requested.
 * After each dataset, the top 10 locations are stored for Task B; use {@link CourseworkMain} as the program entry.
 */
public final class TaskASorting {

    private static final String[] DATASET_FILES = {"candidates_A.csv", "candidates_B.csv", "candidates_C.csv"};

    /** Warm-up sorts per algorithm (JVM / class loading); not included in reported time. */
    private static final int WARMUP_RUNS = 50;

    /** Timed runs per algorithm; wall time and compareTo counts are averaged over this many.
     *  Full run can take a while (especially Bubble Sort on large n); lower for quick local tests. */
    private static final int MEASURED_RUNS = 1000;

    /**
     * Keys: "Dataset A", "Dataset B", "Dataset C". Values: top 10 {@link Location} (own list, not a subList view).
     * Filled when {@link #runSelection} runs; use {@link #getSelectedTop10ByDataset()} for a copy.
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

    public static Map<String, List<Location>> runSelection(File baseDir, boolean printOutput) throws Exception {
        selectedTop10ByDataset.clear();
        SortingService sortingService = new SortingService();

        if (printOutput) {
            System.out.println("Dataset directory: " + baseDir.getAbsoluteFile().getCanonicalPath());
            System.out.println("Ranking: priority_score DESC, tie-break location_id ASC (Location.compareTo)");
            System.out.println("Timing: " + WARMUP_RUNS + " warm-up runs each, then average over " + MEASURED_RUNS + " measured runs (fresh copy each time).");
            System.out.println();
        }

        for (String file : DATASET_FILES) {
            File csvFile = new File(baseDir, file);
            List<Location> list = DataLoader.load(csvFile);
            List<Location> selectedTop = new ArrayList<>();

            if (printOutput) {
                System.out.println("=== " + file + " (" + list.size() + " rows) ===");
                describeDataset(file);
                System.out.println();
            }

            List<SortAlgorithm> algorithms = sortingService.allAlgorithms();
            for (SortAlgorithm alg : algorithms) {
                for (int w = 0; w < WARMUP_RUNS; w++) {
                    List<Location> warm = sortingService.copyList(list);
                    alg.sort(warm);
                }

                long totalNs = 0;
                long totalCompares = 0;
                List<Location> lastCopy = null;
                for (int m = 0; m < MEASURED_RUNS; m++) {
                    List<Location> copy = sortingService.copyList(list);
                    long elapsedNs = alg.sort(copy);
                    totalNs += elapsedNs;
                    totalCompares += alg.getComparisonCount();
                    lastCopy = copy;
                }

                if (lastCopy == null || !SortingService.isSorted(lastCopy)) {
                    throw new IllegalStateException(alg.getName() + " failed sort check");
                }

                selectedTop = new ArrayList<>(SortingService.topN(lastCopy, 10));

                if (printOutput) {
                    double avgMs = (totalNs / (double) MEASURED_RUNS) / 1_000_000.0;
                    double avgCompares = totalCompares / (double) MEASURED_RUNS;
                    System.out.println(alg.getName());
                    System.out.printf("  compareTo calls (avg over %d runs): %.1f%n", MEASURED_RUNS, avgCompares);
                    System.out.printf("  wall time (avg over %d runs): %.6f ms%n", MEASURED_RUNS, avgMs);
                    System.out.println("  top 10 (rank, location_id, priority_score):");
                    for (int r = 0; r < selectedTop.size(); r++) {
                        Location c = selectedTop.get(r);
                        System.out.printf("    %2d  %s  %d%n",
                                r + 1, c.getLocationId(), c.getPriorityScore());
                    }
                    System.out.println();
                }
            }

            String tag = file.substring("candidates_".length(), file.length() - ".csv".length());
            String datasetKey = "Dataset " + tag;
            selectedTop10ByDataset.put(datasetKey, selectedTop);

            if (printOutput) {
                System.out.println();
            }
        }

        return getSelectedTop10ByDataset();
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
