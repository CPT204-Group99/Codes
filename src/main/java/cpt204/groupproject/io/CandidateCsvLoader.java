package cpt204.groupproject.io;

import cpt204.groupproject.model.LocationCandidate;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code location_id,priority_score} rows from candidates CSV files.
 */
public final class CandidateCsvLoader {

    private CandidateCsvLoader() {
    }

    public static List<LocationCandidate> load(Path csvPath) throws IOException {
        List<LocationCandidate> out = new ArrayList<>(1024);
        try (BufferedReader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header == null) {
                throw new IOException("Empty file: " + csvPath);
            }
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                int comma = line.indexOf(',');
                if (comma < 0) {
                    throw new IOException("Malformed line (no comma): " + line);
                }
                String id = line.substring(0, comma).trim();
                int score = Integer.parseInt(line.substring(comma + 1).trim());
                out.add(new LocationCandidate(id, score));
            }
        }
        return out;
    }
}
