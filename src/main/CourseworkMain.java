package main;

import java.io.File;


public final class CourseworkMain {

    public static void main(String[] args) throws Exception {
        File baseDir = new File(args.length > 0 ? args[0] : "Group Project Datasets");
        TaskBShortestPath.runTaskB(baseDir, TaskASorting.runTaskA(baseDir, true));
    }
}
