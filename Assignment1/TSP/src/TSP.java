import java.util.Locale;

public class TSP {

	public static void main(String[] args) {
		Locale.setDefault(new Locale("en", "US"));	
		String dataset = "Burma"; // choose the dataset
		TSPInstance inst = new TSPInstance("data/" + dataset + ".txt"); // load the problem instance
		TSPSolution sol = new TSPSolution(inst, true); // initialize a (random) solution
		//sol.computeGreedy(); // run the greedy algorithm
		//firstIterativeImprovementSwap(inst, sol); // perform iterative improvement
		firstIterativeImprovement2OPT(inst, sol); // perform iterative improvement
		System.out.println("Cost = " + sol.getCost()); // output the cost
		sol.output("output/" + dataset + ".out"); // output the solution
		sol.visualize("figures/" + dataset + ".ipe"); // visualize the solution
	}
	
	
	
	// perform first iterative improvement using the swap local move (solution should be changed!)
	public static void firstIterativeImprovementSwap(TSPInstance inst, TSPSolution sol) {	

		// init local search
		sol.computeGreedy();

		// iterative first improvement
		while (true) {
			double initCost = sol.getCost();
			double currCost = -1;

			// For each possible swap
			for (int i = 0; i < sol.N; i++) {
				for (int j = 0; j < sol.N; j++) {
					sol.applySwap(i,j);
					currCost = initCost;
					// if an improvement was found, make local move
					if (currCost < initCost) {
						break;
					} else {
						sol.undoSwap(i, j);
					}
				}
			}

			// break out of loop if no improvement has been made
			if (currCost >= initCost) {
				break;
			}
		}
	}
	
	
	
	// perform first iterative improvement using the 2-OPT local move (solution should be changed!)
	public static void firstIterativeImprovement2OPT(TSPInstance inst, TSPSolution sol) {

		// init local search
		sol.computeGreedy();

		// iterative first improvement
		while (true) {
			double initCost = sol.getCost();
			double currCost = -1;

			// For each possible 2Opt
			for (int i = 0; i < sol.N; i++) {
				for (int j = 0; j < sol.N; j++) {
					sol.apply2OPT(i, j);
					currCost = initCost;
					// if an improvement was found, make local move
					if (currCost < initCost) {
						break;
					} else {
						sol.undo2OPT(i, j);
					}
				}
			}

			// break out of loop if no improvement has been made
			if (currCost >= initCost) {
				break;
			}
		}
	}
}
