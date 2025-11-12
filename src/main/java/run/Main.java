package run;

import graph.StateSpaceGraph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Deque;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

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
            JsonArray jsonPaths = pathsToJson(paths);
            writeJson(outFile, jsonPaths);

            // Finished
            float elapsed = (finish - start) / 1000.0f / 60.0f;
            ssg.printStats(args[0], paths, numPaths, elapsed);
        }
    }

    /**
     * Converts a list of paths into a JsonArray of paths.
     *
     * @param paths list of paths
     * @return JsonArray of paths.
     */
    public static JsonArray pathsToJson(List<Deque<Integer>> paths) {
        JsonArray outer = new JsonArray();

        for (Deque<Integer> path : paths) {
            JsonArray inner = new JsonArray();
            for (int i = 0; i < path.size(); i++)
                inner.add(i);
            outer.add(inner);
        }

        return outer;
    }

    /**
     * Writes the paths JsonArray to a Json file.
     *
     * @param fileName  output file name
     * @param paths    JsonArray of the generated paths.
     */
    public static void writeJson(String fileName, JsonArray paths) {
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
