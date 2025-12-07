package graph;

import java.util.Arrays;

public class Edge {

    private int src, dst;             // edge's source and destination
    private String transition;        // edge's id and label (operation id)
    private String[] parameters;      // transition operation parameters

    public Edge(int src, int dst, String transition, String[] parameters) {
        this.src = src;
        this.dst = dst;
        this.transition = transition;
        this.parameters = parameters;
    }

    /**
     * Returns the edge source node.
     *
     * @return source node id.
     */
    public int getSrc() {
        return src;
    }


    /**
     * Returns the edge destination node.
     *
     * @return destination node id.
     */
    public int getDst() {
        return dst;
    }

    /**
     * Returns the edge transition operation ID.
     *
     * @return edge transition.
     */
    public String getTransition() {
        return transition;
    }

    /**
     * Returns the transition operation parameters.
     *
     * @return parameters.
     */
    public String[] getParameters() {
        return parameters;
    }

    /** Checks whether the transition operation has parameters.
     *
     * @return true if it has parameters; false otherwise.
     */
    public boolean hasParameters() {
        return parameters.length > 0;
    }

    /**
     * Returns the first parameter of the transition operation.
     *
     * @pre   hasParameters()
     * @return
     */
    public String getFirstParameter() {
        return parameters[0];
    }

    /**
     * Checks whether the transition is a post operation.
     *
     * @return true if it is a post operation; false otherwise.
     */
    public boolean isPost() {
        return transition.toLowerCase().contains("post");
    }

    /**
     * Checks whether the transition is a delete operation.
     *
     * @return true if it is a delete operation; false otherwise.
     */
    public boolean isDelete() {
        return transition.toLowerCase().contains("delete");
    }

    /**
     * Checks whether the edge is equal to the given edge.
     * Two edges are equal if they share the same source and destination.
     *
     * @param object edge to compare.
     * @return true if they're equal; false otherwise
     */
    @Override
    public boolean equals(Object object) {
        return object instanceof Edge other && src == other.getSrc() && dst == other.getDst();
    }

    @Override
    public String toString() {
        return src + " -> " + dst + ": " + transition + Arrays.toString(parameters);
    }
}
