package edu.aa12;

import ilog.cplex.*;
import ilog.concert.*;
import java.util.ArrayList;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

/** CPLEX solver for TSP. */
public class Cplex_TSP {

    public Graph graph;

    public Cplex_TSP(Graph g){
        this.graph = g;
    }

    /** Symmetric TSP, MTZ formulation. */
    public void solve(){
        try {
            IloCplex cplex = new IloCplex();

            // variables
            int vsize = graph.getVertices();
            IloNumVar[] x = cplex.boolVarArray(vsize * vsize);
            double[] c = new double[vsize * vsize];

            int i, j;
            // init costs to inf
            for (i = 0; i < vsize; i++) {
                for (j = 0; j < vsize; j++) {
                    c[i * vsize + j] = Double.MAX_VALUE;
                }
            }

            double cost;
            // set costs for existing edges
            for (Edge e : graph.edges) {
                cost = graph.getLength(e);
                c[e.u * vsize + e.v] = cost;
                c[e.v * vsize + e.u] = cost;
            }

            // add objective function
            IloLinearNumExpr obj = cplex.linearNumExpr();
            for (i = 0; i < vsize; i++) {
                for (j = 0; j < vsize; j++) {
                    obj.addTerm(c[i * vsize + j], x[i * vsize + j]);
                }
            }
            cplex.addMinimize(obj);

            // constraints

            // Row constraint
            for (i = 0; i < vsize; i++) {
                IloLinearNumExpr constraint = cplex.linearNumExpr();
                for (j = 0; j < vsize; j++) {
                    constraint.addTerm(1, x[i * vsize + j]);
                }
                cplex.addEq(constraint, 1);
            }

            // Column constraint
            for (j = 0; j < vsize; j++) {
                IloLinearNumExpr constraint = cplex.linearNumExpr();
                for (i = 0; i < vsize; i++) {
                    constraint.addTerm(1, x[i * vsize + j]);
                }
                cplex.addEq(constraint, 1);
            }

            IloNumVar[] t = cplex.numVarArray(vsize, 0.0, Double.MAX_VALUE);

            for (i = 0; i < vsize; i++) {
                for (j = 1; j < vsize; j++) {
                    IloLinearNumExpr lhs = cplex.linearNumExpr();
                    IloLinearNumExpr rhs = cplex.linearNumExpr();

                    lhs.addTerm(1, t[j]);
                    rhs.addTerm(1, t[i]);

                    rhs.addTerm(vsize, x[i * vsize + j]);
                    rhs.setConstant(1.0 - vsize);
                    cplex.addLe(rhs, lhs);
                }
            }

            // solve
            cplex.setOut(null); // silent mode
            // cplex.exportModel("mymodel.lp"); for debugging
            cplex.solve();
            System.out.println("Objective value (path length): " + cplex.getObjValue());

            cplex.end();

        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    /** Symmetric TSP, subtour formulation. */
    public void solveSubtour() {
        try {
            IloCplex cplex = new IloCplex();

            // variables
            int vsize = graph.getVertices();
            IloNumVar[] x = cplex.boolVarArray(graph.edges.size());
            HashMap<Edge, IloNumVar> xmap = new HashMap<Edge, IloNumVar>();
            int i = 0;
            for (Edge e : graph.edges) {
                xmap.put(e, x[i]);
                i += 1;
            }

            // objective
            IloLinearNumExpr obj = cplex.linearNumExpr();
            for (Edge e : graph.edges) {
                double cost = graph.getLength(e);
                IloNumVar xvar = xmap.get(e);
                obj.addTerm(cost, xvar);
            }
            cplex.addMinimize(obj);

            // constraints
            // all vertices have two edges
            for (i = 0; i < vsize; i++) {
                List<Edge> edges = graph.incidentEdges[i];
                IloLinearNumExpr constraint = cplex.linearNumExpr();
                for (Edge e : edges) {
                    constraint.addTerm(1, xmap.get(e));
                }
                cplex.addEq(constraint, 2);
            }

            // subtour constraint
            // represent subsets through the binary representation of integers.
            // e.g. 100011 means vertex 0,1,5 are included in this subset.
            // NOTE: total bottleneck right now due exponential size
            BigInteger ceil = (new BigInteger("2")).pow(vsize);
            for (BigInteger j = BigInteger.ZERO // j = 0
                ; j.compareTo(ceil) == -1             // j < 2^vsize
                ; j = j.add(BigInteger.ONE)         // j++
                ) {

                int subsetSize = j.bitCount();
                if (subsetSize < 2 || subsetSize >= vsize) {
                    continue;
                }

                IloLinearNumExpr constraint = cplex.linearNumExpr();
                List<Integer> indices = new ArrayList<Integer>();

                // extract all 1-bits from j
                // take advantage of undirected edges where first vertex is always
                // smaller than second edge.
                for (int q = vsize - 1; q >= 0; q--) {
                    if (j.testBit(q)) {
                        double[] edists = graph.distances[q];
                        for (Integer p : indices) {
                            if (edists[p] != Double.POSITIVE_INFINITY) {
                                // there is an edge between p and q
                                // new Edge() hack
                                constraint.addTerm(1, xmap.get(new Edge(q, p)));
                            }
                        }
                        indices.add(q);
                    }
                }
                cplex.addLe(constraint, subsetSize-1);
            }

            // solve
            cplex.setOut(null); // silent mode
            cplex.solve();
            System.out.println("Objective value (path length): " + cplex.getObjValue());
            cplex.end();

        } catch (IloException e) {
            e.printStackTrace();
        }
    }
}
