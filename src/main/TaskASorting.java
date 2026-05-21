package main;

import dataloader.DataLoader;
import model.Location;
import service.AlgorithmBenchmarkResult;
import service.DatasetSortingResult;
import service.SortingService;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Task A console workflow: load CSVs, delegate benchmarks to {@link SortingService}, print results.
 */
public final class TaskASorting {

    private static final String[] DATASET_FILES = {"candidates_A.csv", "candidates_B.csv", "candidates_C.csv"};

    private TaskASorting() {
    }

    public static Map<String, List<Location>> runTaskA(File baseDir, boolean printOutput) throws Exception {
        SortingService sortingService = new SortingService();
        Map<String, List<Location>> top10ByDataset = new LinkedHashMap<>();

        if (printOutput) {
            System.out.println("Dataset directory: " + baseDir.getAbsoluteFile().getCanonicalPath());
            System.out.println("Ranking: priority_score DESC, tie-break location_id ASC (Location.compareTo)");
            System.out.println("Timing: " + SortingService.WARMUP_RUNS + " warm-up runs each, then average over "
                    + SortingService.MEASURED_RUNS + " measured runs (fresh copy each time).");
            System.out.println("Task B Top-10: one sort with the last registered algorithm (Merge Sort).");
            System.out.println();
        }

        for (String file : DATASET_FILES) {
            File csvFile = new File(baseDir, file);
            List<Location> list = DataLoader.load(csvFile);
            DatasetSortingResult result = sortingService.benchmarkDataset(list);

            String datasetKey = toDatasetKey(file);
            top10ByDataset.put(datasetKey, result.getTop10ForTaskB());

            if (printOutput) {
                printDatasetResults(file, list.size(), result);
            }
        }

        return copyTop10Map(top10ByDataset);
    }

    private static void printDatasetResults(String file, int rowCount, DatasetSortingResult result) {
        System.out.println("=== " + file + " (" + rowCount + " rows) ===");
        describeDataset(file);
        System.out.println();

        for (AlgorithmBenchmarkResult benchmark : result.getAlgorithmResults()) {
            System.out.println(benchmark.getAlgorithmName());
            System.out.printf("  compareTo calls (avg over %d runs): %.1f%n",
                    SortingService.MEASURED_RUNS, benchmark.getAverageCompares());
            System.out.printf("  wall time (avg over %d runs): %.6f ms%n",
                    SortingService.MEASURED_RUNS, benchmark.getAverageTimeMs());
            System.out.println("  top 10 (rank, location_id, priority_score):");
            List<Location> preview = benchmark.getTop10Preview();
            for (int r = 0; r < preview.size(); r++) {
                Location c = preview.get(r);
                System.out.printf("    %2d  %s  %d%n", r + 1, c.getLocationId(), c.getPriorityScore());
            }
            System.out.println();
        }

        System.out.println("Task B selection (Merge Sort, single run):");
        List<Location> taskBTop = result.getTop10ForTaskB();
        for (int r = 0; r < taskBTop.size(); r++) {
            Location c = taskBTop.get(r);
            System.out.printf("    %2d  %s  %d%n", r + 1, c.getLocationId(), c.getPriorityScore());
        }
        System.out.println();
    }

    private static String toDatasetKey(String file) {
        String tag = file.substring("candidates_".length(), file.length() - ".csv".length());
        return "Dataset " + tag;
    }

    private static Map<String, List<Location>> copyTop10Map(Map<String, List<Location>> source) {
        Map<String, List<Location>> copy = new LinkedHashMap<>();
        for (Map.Entry<String, List<Location>> entry : source.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
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
