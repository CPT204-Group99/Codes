package main;

import graph.WeightedGraph;
import model.Location;
import model.PathResult;
import service.GraphDataLoader;
import service.ShortestPathService;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class TaskBShortestPath {

    private TaskBShortestPath() {
    }

    public static void main(String[] args) throws Exception {
        File baseDir = new File(args.length > 0 ? args[0] : "Group Project Datasets");

        Map<String, List<Location>> selectedByDataset = TaskASorting.runSelection(baseDir, false);
        WeightedGraph<String> graph = GraphDataLoader.loadUndirectedWeightedGraph(new File(baseDir, "paths.csv"));
        ShortestPathService shortestPathService = new ShortestPathService(graph);

        String datasetA1 = getLocationId(selectedByDataset, "Dataset A", 0);
        String datasetA10 = getLocationId(selectedByDataset, "Dataset A", 9);
        String datasetB1 = getLocationId(selectedByDataset, "Dataset B", 0);
        String datasetB5 = getLocationId(selectedByDataset, "Dataset B", 4);
        String datasetC1 = getLocationId(selectedByDataset, "Dataset C", 0);
        String datasetC5 = getLocationId(selectedByDataset, "Dataset C", 4);

        PathResult case1 = shortestPathService.findShortestPath("Case 1", datasetA1, datasetA1);
        PathResult case2 = shortestPathService.findShortestPath("Case 2", datasetA1, datasetA10);
        PathResult case3 = shortestPathService.findShortestPathVia(
                "Case 3", datasetA1, datasetB1, Arrays.asList(datasetB5));
        PathResult case4 = shortestPathService.findShortestPathVia(
                "Case 4", datasetA1, datasetC1, Arrays.asList(datasetB5, datasetC5));

        System.out.println("Task B - Shortest Path Queries");
        System.out.println("Dataset directory: " + baseDir.getAbsoluteFile().getCanonicalPath());
        System.out.println();

        printSelectedTargets(selectedByDataset);
        printCase(case1);
        printCase(case2);
        printCase(case3);
        printCase(case4);
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
