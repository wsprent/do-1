package edu.aa12;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import edu.aa12.DisjointSet.DSNode;

/** Class for finding the minimum spanning tree using Kruskals algorithm */
public class Kruskal {
    private final DSNode[] nodes = new DSNode[200];//Hack
    private final DisjointSet ds = new DisjointSet();


    /**
     * Find the minimum spanning tree in a graph where included edges in <code>node</code> are
     * contracted and excluded edges in <code>node</code> are disregarded.
     */
    public List<Edge> minimumSpanningTree(final Graph graph, BnBNode node) {
        for(Edge e: graph.edges){
            nodes[e.u] = ds.makeSet(e.u);
            nodes[e.v] = ds.makeSet(e.v);
        }

        List<Edge> mstEdges = new LinkedList<Edge>(graph.edges);
        BnBNode n = node;
        while(n.parent!=null){
            if(n.edgeIncluded) ds.union(nodes[n.edge.u], nodes[n.edge.v]);        //Contract included edges
            else               mstEdges.remove(n.edge);                        //Disregard excluded edges
            n=n.parent;
        }

        List<Edge> tmp = new ArrayList<Edge>(mstEdges);
        Collections.sort(tmp, new Comparator<Edge>(){    //Sort edges in nondescending order
            public int compare(Edge o1, Edge o2) {
                return Double.compare(graph.getDistance(o1.u, o1.v), graph.getDistance(o2.u, o2.v));
            }});

        for(Edge e: tmp){ //Main loop of Kruskal
            if(ds.find(nodes[e.u])!=ds.find(nodes[e.v])){
                ds.union(nodes[e.u], nodes[e.v]);
            }else{
                mstEdges.remove(e);
            }
        }

        return mstEdges;
    }

    /** Finds some arbitrary 1-tree that adheres to the constraints set by the given BnBNode
     *  Minimum spanning tree implementation with some minor modifications. */
    public List<Edge> arbitrary1Tree(final Graph graph, BnBNode node) {

        boolean[] includedVertices = new boolean[200]; // someone already did this hack :P
        for(Edge e: graph.edges){
            nodes[e.u] = ds.makeSet(e.u);
            nodes[e.v] = ds.makeSet(e.v);
        }

        // contract included edges and ignore excluded edges from BnBNode
        List<Edge> mstEdges = new ArrayList<Edge>(graph.edges);
        BnBNode n = node;
        while(n.parent!=null){
            if(n.edgeIncluded) {
                ds.union(nodes[n.edge.u], nodes[n.edge.v]);
                includedVertices[n.edge.u] = true;
                includedVertices[n.edge.v] = true;
            } else {
                mstEdges.remove(n.edge);
            }
            n=n.parent;
        }

        // find first vertex that isn't covered by BnBNode
        int vertex1 = -1;
        for (int i = 0; i < 200; i++) {
            if (!includedVertices[i]) {
                vertex1 = i;
                break;
            }
        }

        Comparator<Edge> comp = new Comparator<Edge>(){    //Sort edges in nondescending order
            public int compare(Edge o1, Edge o2) {
                return Double.compare(graph.getDistance(o1.u, o1.v), graph.getDistance(o2.u, o2.v));
            }};
        Collections.sort(mstEdges, comp);

        List<Edge> result = new ArrayList<Edge>();
        List<Edge> edges1 = new ArrayList<Edge>();
        for (Edge e: mstEdges) { //Main loop of Kruskal
            if (e.u == vertex1 || e.v == vertex1) {
                // don't include edges that touch vertex 1
                edges1.add(e);
                continue;
            }
            if (ds.find(nodes[e.u])!=ds.find(nodes[e.v])) {
                ds.union(nodes[e.u], nodes[e.v]);
                result.add(e);
            }
        }

        // reconnect vertex1 to the tree, 1-tree style
        if (edges1.size() >= 2) {
            Collections.sort(edges1, comp);
            result.add(edges1.get(0));
            result.add(edges1.get(1));
        }
        return result;
    }

//    public List<Edge> minimumSpanningTree(final Graph g, List<Edge> edges){
//
//        List<Edge> ret = new ArrayList<Edge>(edges);
//        Collections.sort(edges, new Comparator<Edge>(){
//            public int compare(Edge o1, Edge o2) {
//                return Double.compare(g.getDistance(o1.u, o1.v), g.getDistance(o2.u, o2.v));
//            }});
//
//        for(Edge e: edges){
//            nodes[e.u] = ds.makeSet(e.u);
//            nodes[e.v] = ds.makeSet(e.v);
//        }
//        for(Edge e: edges){
//            if(ds.find(nodes[e.u])!=ds.find(nodes[e.v])){
//                ds.union(nodes[e.u], nodes[e.v]);
//            }else{
//                ret.remove(e);
//            }
//        }
//
//        return ret;
//    }

}
