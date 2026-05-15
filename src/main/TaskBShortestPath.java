package main;

import graph.WeightedGraph;
import model.Location;
import model.PathResult;
import io.GraphDataLoader;
import service.DmsyShortestPathService;
import service.ShortestPathService;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Task B: shortest-path queries using Task A's selected top-10 locations; invoked from {@link CourseworkMain}. */
public final class TaskBShortestPath {

    private TaskBShortestPath() {
    }

    /**
     * Prints shortest-path cases for Task B using locations already chosen by Task A.
     * Does not call {@link TaskASorting#runSelection}; pass the map returned from a single {@code runSelection} call.
     */
    public static void runTaskB(File baseDir, Map<String, List<Location>> selectedByDataset) throws Exception {
        WeightedGraph<String> graph = GraphDataLoader.loadUndirectedWeightedGraph(new File(baseDir, "paths.csv"));
        ShortestPathService paths = new ShortestPathService(graph);
        DmsyShortestPathService dmsyPaths = new DmsyShortestPathService(graph);

        String datasetA1 = getLocationId(selectedByDataset, "Dataset A", 0);
        String datasetA10 = getLocationId(selectedByDataset, "Dataset A", 9);
        String datasetB1 = getLocationId(selectedByDataset, "Dataset B", 0);
        String datasetB5 = getLocationId(selectedByDataset, "Dataset B", 4);
        String datasetC1 = getLocationId(selectedByDataset, "Dataset C", 0);
        String datasetC5 = getLocationId(selectedByDataset, "Dataset C", 4);

        System.out.println("Task B - Shortest Path Queries");
        System.out.println("Dataset directory: " + baseDir.getAbsoluteFile().getCanonicalPath());
        System.out.println();

        printSelectedTargets(selectedByDataset);

        System.out.println("=== Dijkstra ===");
        System.out.println();
        printDijkstraCases(paths, datasetA1, datasetA10, datasetB1, datasetB5, datasetC1, datasetC5, "");

        System.out.println("=== Bellman-Ford ===");
        System.out.println();
        printBellmanFordCases(paths, datasetA1, datasetA10, datasetB1, datasetB5, datasetC1, datasetC5, " [Bellman-Ford]");

        System.out.println("=== Dijkstra (binary heap) ===");
        System.out.println();
        printDijkstraHeapCases(paths, datasetA1, datasetA10, datasetB1, datasetB5, datasetC1, datasetC5,
                " [Dijkstra-heap]");

        System.out.println("=== explore: BMSSP (DMSY arXiv:2504.17033) ===");
        System.out.println("(explore 实现：在课程数据集上尚未得到完整 SSSP，结果可能与 Dijkstra 不一致)");
        System.out.println();
        printDmsyCases(dmsyPaths, paths, datasetA1, datasetA10, datasetB1, datasetB5, datasetC1, datasetC5,
                " [BMSSP/DMSY]");
    }

    private static void printDijkstraCases(ShortestPathService paths,
                                           String datasetA1,
                                           String datasetA10,
                                           String datasetB1,
                                           String datasetB5,
                                           String datasetC1,
                                           String datasetC5,
                                           String caseSuffix) {
        PathResult case1 = paths.findShortestPath("Case 1" + caseSuffix, datasetA1, datasetA1);
        PathResult case2 = paths.findShortestPath("Case 2" + caseSuffix, datasetA1, datasetA10);
        PathResult case3 = paths.findShortestPathVia(
                "Case 3" + caseSuffix, datasetA1, datasetB1, Arrays.asList(datasetB5));
        PathResult case4 = paths.findShortestPathVia(
                "Case 4" + caseSuffix, datasetA1, datasetC1, Arrays.asList(datasetB5, datasetC5));

        printCase(case1);
        printCase(case2);
        printCase(case3);
        printCase(case4);
    }

    private static void printBellmanFordCases(ShortestPathService paths,
                                              String datasetA1,
                                              String datasetA10,
                                              String datasetB1,
                                              String datasetB5,
                                              String datasetC1,
                                              String datasetC5,
                                              String caseSuffix) {
        PathResult case1 = paths.findShortestPathBellmanFord("Case 1" + caseSuffix, datasetA1, datasetA1);
        PathResult case2 = paths.findShortestPathBellmanFord("Case 2" + caseSuffix, datasetA1, datasetA10);
        PathResult case3 = paths.findShortestPathViaBellmanFord(
                "Case 3" + caseSuffix, datasetA1, datasetB1, Arrays.asList(datasetB5));
        PathResult case4 = paths.findShortestPathViaBellmanFord(
                "Case 4" + caseSuffix, datasetA1, datasetC1, Arrays.asList(datasetB5, datasetC5));

        printCase(case1);
        printCase(case2);
        printCase(case3);
        printCase(case4);
    }

    private static void printDijkstraHeapCases(ShortestPathService paths,
                                               String datasetA1,
                                               String datasetA10,
                                               String datasetB1,
                                               String datasetB5,
                                               String datasetC1,
                                               String datasetC5,
                                               String caseSuffix) {
        PathResult case1 = paths.findShortestPathDijkstraHeap("Case 1" + caseSuffix, datasetA1, datasetA1);
        PathResult case2 = paths.findShortestPathDijkstraHeap("Case 2" + caseSuffix, datasetA1, datasetA10);
        PathResult case3 = paths.findShortestPathViaDijkstraHeap(
                "Case 3" + caseSuffix, datasetA1, datasetB1, Arrays.asList(datasetB5));
        PathResult case4 = paths.findShortestPathViaDijkstraHeap(
                "Case 4" + caseSuffix, datasetA1, datasetC1, Arrays.asList(datasetB5, datasetC5));

        printCase(case1);
        printCase(case2);
        printCase(case3);
        printCase(case4);
    }

    private static void printDmsyCases(DmsyShortestPathService dmsyPaths,
                                       ShortestPathService dijkstraPaths,
                                       String datasetA1,
                                       String datasetA10,
                                       String datasetB1,
                                       String datasetB5,
                                       String datasetC1,
                                       String datasetC5,
                                       String caseSuffix) {
        runDmsyCase(dmsyPaths, dijkstraPaths, "Case 1" + caseSuffix, datasetA1, datasetA1, List.of());
        runDmsyCase(dmsyPaths, dijkstraPaths, "Case 2" + caseSuffix, datasetA1, datasetA10, List.of());
        runDmsyCase(dmsyPaths, dijkstraPaths, "Case 3" + caseSuffix, datasetA1, datasetB1, Arrays.asList(datasetB5));
        runDmsyCase(dmsyPaths, dijkstraPaths, "Case 4" + caseSuffix, datasetA1, datasetC1,
                Arrays.asList(datasetB5, datasetC5));
    }

    private static void runDmsyCase(DmsyShortestPathService dmsyPaths,
                                    ShortestPathService dijkstraPaths,
                                    String caseName,
                                    String start,
                                    String destination,
                                    List<String> vias) {
        try {
            PathResult result = vias.isEmpty()
                    ? dmsyPaths.findShortestPath(caseName, start, destination)
                    : dmsyPaths.findShortestPathVia(caseName, start, destination, vias);
            printCase(result);
        } catch (RuntimeException ex) {
            PathResult ref = vias.isEmpty()
                    ? dijkstraPaths.findShortestPath(caseName + " (Dijkstra ref)", start, destination)
                    : dijkstraPaths.findShortestPathVia(caseName + " (Dijkstra ref)", start, destination, vias);
            System.out.println(caseName);
            System.out.println("  BMSSP failed: " + ex.getMessage());
            System.out.printf("  (Dijkstra reference: cost %s, path %s)%n",
                    formatCost(ref.getTotalCost()), String.join(" -> ", ref.getPath()));
            System.out.println();
        }
    }

    private static String getLocationId(Map<String, List<Location>> selectedByDataset, String datasetKey, int index) {
        List<Location> locations = selectedByDataset.get(datasetKey);
        if (locations == null || locations.size() <= index) {
            throw new IllegalStateException("Missing selected locations for " + datasetKey);
        }
        return locations.get(index).getLocationId();
    }

    private static void printSelectedTargets(Map<String, List<Location>> selectedByDataset) {
        System.out.println("Selected top 10 targets from Task A:");
        for (Map.Entry<String, List<Location>> entry : selectedByDataset.entrySet()) {
            System.out.print("  " + entry.getKey() + ": ");
            for (int i = 0; i < entry.getValue().size(); i++) {
                if (i > 0) {
                    System.out.print(", ");
                }
                System.out.print(entry.getValue().get(i).getLocationId());
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void printCase(PathResult result) {
        System.out.println(result.getCaseName());
        System.out.println("  start: " + result.getStartLocationId());
        System.out.println("  destination: " + result.getDestinationLocationId());
        if (!result.getViaLocationIds().isEmpty()) {
            System.out.println("  required waypoint(s): " + String.join(" -> ", result.getViaLocationIds()));
        }
        System.out.println("  shortest path: " + String.join(" -> ", result.getPath()));
        System.out.println("  total path cost: " + formatCost(result.getTotalCost()));
        System.out.printf("  algorithm time (System.nanoTime): %.6f ms%n",
                result.getAlgorithmElapsedNanos() / 1_000_000.0);
        System.out.println();
    }

    private static String formatCost(double cost) {
        long rounded = Math.round(cost);
        if (Math.abs(cost - rounded) < 1e-9) {
            return Long.toString(rounded);
        }
        return Double.toString(cost);
    }
}
