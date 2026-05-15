package dataloader;

import graph.WeightedEdge;
import graph.WeightedGraph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GraphDataLoader {

    public static WeightedGraph<String> loadUndirectedWeightedGraph(File csvFile) throws IOException {
        Map<String, Integer> indexByLocationId = new LinkedHashMap<>();
        List<String> vertices = new ArrayList<>();
        List<WeightedEdge> edges = new ArrayList<>();

        try (Scanner scan = new Scanner(csvFile)) {
            if (!scan.hasNextLine()) {
                throw new IOException("empty file: " + csvFile.getPath());
            }
            scan.nextLine();

            while (scan.hasNextLine()) {
                String line = scan.nextLine().trim();
                if (line.length() == 0) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length != 3) {
                    throw new IOException("invalid path row: " + line);
                }

                String fromId = parts[0].trim();
                String toId = parts[1].trim();
                double weight = Double.parseDouble(parts[2].trim());

                int fromIndex = indexFor(fromId, indexByLocationId, vertices);
                int toIndex = indexFor(toId, indexByLocationId, vertices);

                edges.add(new WeightedEdge(fromIndex, toIndex, weight));
                edges.add(new WeightedEdge(toIndex, fromIndex, weight));
            }
        }

        return new WeightedGraph<>(vertices, edges);
    }

    private static int indexFor(String locationId,
                                Map<String, Integer> indexByLocationId,
                                List<String> vertices) {
        Integer existingIndex = indexByLocationId.get(locationId);
        if (existingIndex != null) {
            return existingIndex;
        }

        int newIndex = vertices.size();
        vertices.add(locationId);
        indexByLocationId.put(locationId, newIndex);
        return newIndex;
    }
}
