package edu.aa12;

public class MainMethods {

    public static void main(String[] args){
        int caseNumber = 1;
        if (args.length > 0) {
            try { caseNumber = Integer.parseInt(args[0]); }
            catch (Exception e) {}
        }

        Graph instance = new Instance1();
        if (caseNumber == 2) {
            instance = new Instance2();
        }
        if (caseNumber == 3) {
            instance = new Instance3();
        }

        System.out.println("\nBRANCH and BOUND\n");
        solveGraph(instance);

        System.out.println("\nCPLEX\n");
        cplexSolveGraph(instance);

        //System.out.println("\nUpper bound for Instance1: "
        //        + Double.toString((new Instance1()).getUpperBound()));
    }

    public static void solveGraph(Graph g) {
        BranchAndBound_TSP solver = new BranchAndBound_TSP(g);
        long start = System.nanoTime();
        BnBNode n = solver.solve();
        long end = System.nanoTime();
        System.out.printf("Took %.2fms\n",(end-start)/1000000.0);
        // System.out.println(n);
        // Visualization.visualizeSolution(g, n);//Requires ProGAL (www.diku.dk/~rfonseca/ProGAL)
    }

    public static void cplexSolveGraph(Graph g) {
        Cplex_TSP solver = new Cplex_TSP(g);
        long start = System.nanoTime();
        solver.solve();
        long end = System.nanoTime();
        System.out.printf("Took %.2fms\n",(end-start)/1000000.0);
    }
}
