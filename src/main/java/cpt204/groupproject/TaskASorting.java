package cpt204.groupproject;

import cpt204.groupproject.io.CandidateCsvLoader;
import cpt204.groupproject.model.LocationCandidate;
import cpt204.groupproject.sort.CandidateOrdering;
import cpt204.groupproject.sort.CountingComparator;
import cpt204.groupproject.sort.SortingAlgorithms;
import cpt204.groupproject.sort.SortingVerifier;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Task A: load candidates A/B/C, sort with Bubble / Quick / Merge, print top 10 and benchmark counts.
 * <p>
 * Ranking: {@link CandidateOrdering#comparator()} — highest {@code priority_score} first;
 * ties broken by lexicographically smaller {@code location_id}.
 * </p>
 */
public final class TaskASorting {

    private TaskASorting() {
    }

    public static void main(String[] args) throws Exception {
        Path base = Path.of(args.length > 0 ? args[0] : "Group Project Datasets");
        String[] files = {"candidates_A.csv", "candidates_B.csv", "candidates_C.csv"};
        Comparator<LocationCandidate> baseCmp = CandidateOrdering.comparator();

        System.out.println("Dataset directory: " + base.toAbsolutePath().normalize());
        System.out.println("Ranking: priority_score DESC, tie-break location_id ASC");
        System.out.println();

        for (String file : files) {
            Path path = base.resolve(file);
            List<LocationCandidate> list = CandidateCsvLoader.load(path);
            LocationCandidate[] original = list.toArray(LocationCandidate[]::new);

            System.out.println("=== " + file + " (" + original.length + " rows) ===");
            describeDataset(file, original);
            System.out.println();

            runAlgorithm("Bubble Sort", original, baseCmp, SortingAlgorithms::bubbleSort);
            runAlgorithm("Quick Sort", original, baseCmp, SortingAlgorithms::quickSort);
            runAlgorithm("Merge Sort", original, baseCmp, SortingAlgorithms::mergeSort);
            System.out.println();
        }
    }

    private static void describeDataset(String file, LocationCandidate[] data) {
        String tag = file.substring("candidates_".length(), file.length() - ".csv".length());
        switch (tag) {
            case "A" -> System.out.println(
                    "Property: mostly pre-sorted by score descending (with small local inversions); "
                            + "Bubble Sort benefits from early exit after few passes.");
            case "B" -> System.out.println(
                    "Property: pseudo-random order; no favourable structure for Bubble Sort; "
                            + "Quick/Merge expected to dominate asymptotically.");
            case "C" -> System.out.println(
                    "Property: heavy ties on score; tie-break by location_id makes order total; "
                            + "Merge Sort is stable (stability matters when secondary key is implicit).");
            default -> System.out.println("Property: (see brief for dataset " + tag + ").");
        }
    }

    private interface SortAction {
        void sort(LocationCandidate[] arr, Comparator<? super LocationCandidate> cmp);
    }

    private static void runAlgorithm(
            String label,
            LocationCandidate[] original,
            Comparator<LocationCandidate> baseCmp,
            SortAction action) {

        LocationCandidate[] copy = Arrays.copyOf(original, original.length);
        CountingComparator<LocationCandidate> cmp = new CountingComparator<>(baseCmp);

        long t0 = System.nanoTime();
        action.sort(copy, cmp);
        long elapsedNs = System.nanoTime() - t0;

        if (!SortingVerifier.isSorted(copy, baseCmp)) {
            throw new IllegalStateException(label + " failed sort verification");
        }

        System.out.println(label);
        System.out.printf(Locale.ROOT, "  comparisons (via Comparator): %,d%n", cmp.comparisons());
        System.out.printf(Locale.ROOT, "  wall time: %.3f ms%n", elapsedNs / 1_000_000.0);
        System.out.println("  top 10 (rank, location_id, priority_score):");
        for (int r = 0; r < 10 && r < copy.length; r++) {
            LocationCandidate c = copy[r];
            System.out.printf(Locale.ROOT, "    %2d  %s  %d%n", r + 1, c.locationId(), c.priorityScore());
        }
        System.out.println();
    }
}
