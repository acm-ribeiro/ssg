package run;

import graph.Edge;
import graph.StateSpaceGraph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Deque;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Main {

    public static void main(String[] args) {
        if(args.length != 3)
            usage();
        else {
            // Counting time
            long start = System.currentTimeMillis();

            // Reading input
            String dotFile = args[0];
            int numPaths  = Integer.parseInt(args[1]);
            String outFile = args[2];

            // Generating paths
            StateSpaceGraph ssg = new StateSpaceGraph(dotFile);
            List<Deque<Integer>> paths = ssg.getPaths(numPaths);
            long finish = System.currentTimeMillis();

            // Writing to JSON file
            JsonObject testSuite = pathsToJson(ssg, paths);
            writeJson(outFile, testSuite);

            // Finished
            float elapsed = (finish - start) / 1000.0f / 60.0f;
            ssg.printStats(args[0], paths, numPaths, elapsed);
        }
    }

    /**
     * Converts a list of paths into a Test Suite.
     *
     * @param paths list of paths
     * @return JsonObject representing a test suite (list of sequences).
     */
    public static JsonObject pathsToJson(StateSpaceGraph ssg, List<Deque<Integer>> paths) {
        JsonObject testSuite = new JsonObject();
        int sequenceId = 0;

        for (Deque<Integer> path : paths) {
            JsonArray test = new JsonArray();
            Edge[] edges = ssg.getPathEdges(path);
            JsonObject edge = new JsonObject();

            for (Edge e : edges) {
                JsonArray params = new JsonArray();

                for (String param : e.getParameters())
                    params.add(param);

                edge.addProperty("operationId", e.getTransition());
                edge.add("parameters", params);
                test.add(edge);
            }

            testSuite.add(String.valueOf(sequenceId++) , test);
        }

        return testSuite;
    }

    /**
     * Writes the paths JsonArray to a Json file.
     *
     * @param fileName  output file name
     * @param paths    JsonObject representing a test suite composed of the generated paths.
     */
    public static void writeJson(String fileName, JsonObject paths) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + ".json"))) {
            new GsonBuilder().setPrettyPrinting().create().toJson(paths, writer);
        } catch (IOException e) {
            System.err.println("Could not write to file " + fileName);
        }
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
            """
        );

        System.exit(1);
    }

}
