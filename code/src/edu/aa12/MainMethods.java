package edu.aa12;

public class MainMethods {

	public static void main(String[] args){

		// branch and bound
		System.out.println("\nBRANCH and BOUND\n");
		solveGraph(new Instance1());
		// solveGraph(new Instance2());
		// solveGraph(new Instance3());

		// cplex
		System.out.println("\nCPLEX\n");
		cplexSolveGraph(new Instance1());
		// cplexSolveGraph(new Instance2());
		// cplexSolveGraph(new Instance3());
	}

	public static void solveGraph(Graph g) {
		BranchAndBound_TSP solver = new BranchAndBound_TSP(g);
		long start = System.nanoTime();
		BnBNode n = solver.solve();
		long end = System.nanoTime();
		System.out.printf("Took %.2fms\n",(end-start)/1000000.0);
		System.out.println(n);
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
