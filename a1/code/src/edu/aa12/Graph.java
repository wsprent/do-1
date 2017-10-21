package edu.aa12;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/** A representation of an undirected graph. Vertices are implicitly represented as numbers from 0 to (getVertices()-1). */
public abstract class Graph {
    public final double[][] vertexCoords;
    /** An array of lists. The i'th entry indicates the edges adjacent to vertex i */
    public final List<Edge> edges;
    /** An array of lists. The i'th entry indicates the edges adjacent to vertex i */
    public final List<Edge>[] incidentEdges;
    protected final double[][] distances;
    public final Comparator<Edge> edgeComparator;

    @SuppressWarnings("unchecked")
    Graph(double[][] coords){
        this.vertexCoords = coords;
        int n = vertexCoords.length;
        this.edges = new ArrayList<Edge>();
        this.incidentEdges = new List[n];
        for(int i=0;i<n;i++) incidentEdges[i]=new LinkedList<Edge>();
        this.distances = new double[n][n];
        for(int i=0;i<n;i++) for(int j=0;j<n;j++) distances[i][j] = Double.POSITIVE_INFINITY;
        this.edgeComparator = new Comparator<Edge>(){    //Sort edges in nondescending order
            public int compare(Edge o1, Edge o2) {
                return Double.compare(getDistance(o1.u, o1.v), getDistance(o2.u, o2.v));
            }};
    }

    public int getVertices(){ return vertexCoords.length; }

    public double getDistance(int i, int j){ return distances[i][j]; }

    public double getLength(Edge e){
        return distances[e.u][e.v];
    }

    // assumes complete graph, greedily selects hamiltonian path
    public double getUpperBound() {
        Comparator<Edge> comp = edgeComparator;
        double bound = 0.0;
        int n = getVertices();
        int currentVertex = 0;
        boolean[] vertices = new boolean[n];
        vertices[currentVertex] = true;

        for (int i = 0; i < n-1; i++) {
            List<Edge> nextEdges = incidentEdges[currentVertex];
            Collections.sort(nextEdges, comp);
            Edge next = null;
            for (Edge e : nextEdges) {
                int other = e.u == currentVertex ? e.v : e.u;
                if (!vertices[other]) {
                    // not included this vertex yet
                    vertices[other] = true;
                    next = e;
                    currentVertex = other;
                    break;
                }
            }
            bound += getLength(next);
        }
        for (Edge e: incidentEdges[currentVertex]) {
            if (e.u == 0 || e.v == 0) {
                // final edge
                bound += getLength(e);
                break;
            }
        }
        return bound;
    }

    protected void createEdge(int i, int j){
        if(distances[i][j]<Double.POSITIVE_INFINITY) return;
        double dx = vertexCoords[i][0]-vertexCoords[j][0];
        double dy = vertexCoords[i][1]-vertexCoords[j][1];
        distances[i][j] = distances[j][i] = Math.sqrt( dx*dx+dy*dy );
        Edge e = new Edge(i,j);
        edges.add(e);
        incidentEdges[i].add(e);
        incidentEdges[j].add(e);

    }
}
