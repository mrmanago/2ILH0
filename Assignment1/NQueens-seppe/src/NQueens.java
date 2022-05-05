
public class NQueens {

	public static void main(String[] args) {
		String dataset = "8"; // choose the dataset
		NQueenInstance inst = new NQueenInstance("data/" + dataset + ".txt"); // load the problem instance
		//NQBasicSol sol = new NQBasicSol(inst, true); // initialize a (random) solution
		NQPermSol sol = new NQPermSol(inst, true); // initialize a (random) solution
		sol.visualize("figures/" + dataset + "_intial.png");
		bestIterativeImprovement(inst, sol);
		System.out.println("Cost = " + sol.getCost());
		sol.visualize("figures/" + dataset + ".png");
		sol.output("output/" + dataset + ".txt");
	}
	
	

	// perform best iterative improvement (solution should be changed directly)
	public static void bestIterativeImprovement(NQueenInstance inst, NQBasicSol sol) {
		
		// TODO
		int iterations = 100;
		int queen = -1;
		int move = -1;
		int bestCost = sol.getCost();
		int tempCost = 0;

		// Run the iterations of the algorithm
		for (int i=0; i<iterations; i++) {
			if (bestCost == 0) {
				break;
			}

			// For every queen
			for (int q=0; q < inst.N; q++) {
				// Try every move
				for (int m=0; m < 8; m++) {
					// Apply the move if possible
					if (sol.applyLocalMove(q, m)) {
						// Get the cost of the solution after the move
						tempCost = sol.getCost();
						// When a move is found that is better than the current best, update it
						if (tempCost < bestCost) {
							bestCost = tempCost;
							queen = q;
							move = m;
						}
						// Undo the move
						sol.undoLocalMove(q, m);
					}
				}
			}

			// Apply the best move
			sol.applyLocalMove(queen, move);
		}
	}
	
	

	// perform best iterative improvement (solution should be changed directly)
	public static void bestIterativeImprovement(NQueenInstance inst, NQPermSol sol) {
		
		// TODO
		int iterations = 100;
		int bestCost = sol.getCost();
		int tempCost = 0;

		// Run the iterations of the algorithm
		for (int i=0; i<iterations; i++) {
			if (bestCost == 0) {
				break;
			}

			// Set move to standard values
			boolean swapRow = false;
			int swapA = -1;
			int swapB = -1;

			// Try every row swap
			for (int a = 0; a < inst.N - 1; a++) {
				for (int b = a + 1; b < inst.N; b++) {
					// Apply the move if possible
					if (sol.applyLocalMove(a, b, true)) {
						// Get the cost of the solution after the move
						tempCost = sol.getCost();
						// When a move is found that is better than the current best, update it
						if (tempCost <= bestCost) {
							bestCost = tempCost;
							swapA = a;
							swapB = b;
							swapRow = true;
						}
						// Undo the move
						sol.undoLocalMove(a, b, true);
					}
				}
			}

			// Try every column swap
			for (int a = 0; a < inst.N - 1; a++) {
				for (int b = a + 1; b < inst.N; b++) {
					// Apply the move if possible
					if (sol.applyLocalMove(a, b, false)) {
						// Get the cost of the solution after the move
						tempCost = sol.getCost();
						// When a move is found that is better than the current best, update it
						if (tempCost <= bestCost) {
							bestCost = tempCost;
							swapA = a;
							swapB = b;
							swapRow = false;
						}
						// Undo the move
						sol.undoLocalMove(a, b, false);
					}
				}
			}

			// Apply the best move if one was found
			if (swapA < 0 || swapB < 0) {
				break;
			}
			System.out.println("Best cost was: " + bestCost + ", with move: " + swapA + ", " + swapB + ", " + swapRow);
			sol.visualize("figures/8_move" + i + ".png");
			sol.applyLocalMove(swapA, swapB, swapRow);
		}
	}
	
}
