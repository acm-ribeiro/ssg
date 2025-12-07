package run;

import graph.Edge;
import graph.StateSpaceGraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Deque;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Main {
    public static void main(String[] args) {
        if (args.length != 3) {
            usage();
        } else {
            // Counting time
            long start = System.currentTimeMillis();

            // Reading input
            String dotFile = args[0];
            int numPaths = Integer.parseInt(args[1]);
            String outFile = args[2];

            // Generating paths
            StateSpaceGraph ssg = new StateSpaceGraph(dotFile);
            List<Deque<Integer>> paths = ssg.getPathsOptimised(numPaths);

            List<List<Edge>> withPuts = ssg.addUpdates(paths);
            StringBuilder sb = new StringBuilder();
            for (List<Edge> path : withPuts) {
                sb.append("{");
                for (Edge edge : path){
                    sb.append(edge.getTransition());
                    sb.append(Arrays.toString(edge.getParameters()));
                    sb.append(", ");
                }
                sb = new StringBuilder(sb.substring(0, sb.length() - 2));
                sb.append("}\n");
            }
            System.out.println(sb);
            long finish = System.currentTimeMillis();

//            // Writing to JSON file
//            JsonArray testSuite = pathsToJson(ssg, paths);
//            writeJson(outFile, testSuite);
//
//            // Get file size
//            File jsonFile = new File(args[2] + ".json");  // args[2] is your json path
//            long fileSizeBytes = jsonFile.length();
//            String fileSize = formatFileSize(fileSizeBytes);
//
//            // Finished
//            long elapsedMillis = finish - start;
//            long elapsedSeconds = elapsedMillis / 1000;
//            long minutes = elapsedSeconds / 60;
//            long seconds = elapsedSeconds % 60;
//
//            String elapsed = String.format("%dmin%02dsec", minutes, seconds);
//            ssg.printStats(args[0], paths, numPaths, elapsed, fileSize);
        }
    }

    /**
     * Converts a list of paths into a Test Suite.
     *
     * @param paths list of paths
     * @return JsonArray representing a test suite (list of sequences).
     */
    public static JsonArray pathsToJson(StateSpaceGraph ssg, List<Deque<Integer>> paths) {
        JsonArray testSuite = new JsonArray();
        int sequenceId = 0;

        for (Deque<Integer> path : paths) {
            JsonArray sequence = new JsonArray();
            Edge[] edges = ssg.getPathEdges(path);

            for (Edge e : edges) {
                JsonObject edge = new JsonObject();
                JsonArray params = new JsonArray();

                for (String param : e.getParameters()) {
                    params.add(param);
                }

                edge.addProperty("operationId", e.getTransition());
                edge.add("parameters", params);
                sequence.add(edge);
            }

            JsonObject test = new JsonObject();
            test.addProperty("sequenceId", sequenceId++);
            test.add("sequence", sequence);

            testSuite.add(test);
        }

        return testSuite;
    }

    /**
     * Writes the paths JsonArray to a Json file.
     *
     * @param fileName output file name
     * @param paths JsonArray representing a test suite composed of the generated paths.
     */
    public static void writeJson(String fileName, JsonArray paths) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + ".json"))) {
            new GsonBuilder().setPrettyPrinting().create().toJson(paths, writer);
        } catch (IOException e) {
            System.err.println("Could not write to file " + fileName);
        }
    }

    /**
     * Formats file size in human-readable format (B, KB, MB, GB).
     */
    private static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + "B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char unit = "KMGT".charAt(exp - 1);

        return String.format("%.1f%cB", bytes / Math.pow(1024, exp), unit);
    }

    /**
     * Prints a usage message in case the user didn't provide the right amount of arguments.
     */
    private static void usage() {
        System.err.println("""
            
            Usage: java -jar ssg.jar <dot_file> <num_paths> <json_output_file>
              <dot_file>          : input graph file (in DOT format)\s
              <num_paths>        : positive integer (maximum number of paths to generate)
              <json_output_file>  : output .json file path
            """);

        System.exit(1);
    }
}