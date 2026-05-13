package service;

import model.Location;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DataLoader {

    public static List<Location> load(File csvFile) throws IOException {
        ArrayList<Location> list = new ArrayList<Location>();
        Scanner scan = new Scanner(csvFile);

        if (!scan.hasNextLine()) {
            scan.close();
            throw new IOException("empty file: " + csvFile.getPath());
        }
        scan.nextLine();

        while (scan.hasNextLine()) {
            String line = scan.nextLine().trim();
            if (line.length() > 0) {
                int comma = line.indexOf(',');
                if (comma < 0) {
                    scan.close();
                    throw new IOException("no comma in line: " + line);
                }
                String id = line.substring(0, comma).trim();
                String scoreStr = line.substring(comma + 1).trim();
                int score = Integer.parseInt(scoreStr);
                list.add(new Location(id, score));
            }
        }

        scan.close();
        return list;
    }
}
