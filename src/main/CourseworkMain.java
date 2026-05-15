package main;

import java.io.File;

/**
 * Single program entry: runs Task A (full sorting benchmarks and output), then Task B
 * using the same {@link TaskASorting#runSelection} result without a second pass.
 */
public final class CourseworkMain {

    private CourseworkMain() {
    }

    public static void main(String[] args) throws Exception {
        File baseDir = new File(args.length > 0 ? args[0] : "Group Project Datasets");
        TaskBShortestPath.runTaskB(baseDir, TaskASorting.runSelection(baseDir, true));
    }
}
