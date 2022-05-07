import java.util.ArrayList;
import java.util.Random;

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
		// Variables responsible for picking a random best swap
		Random rand = new Random();
		int solNumber;

		// Run the iterations of the algorithm
		for (int i=0; i<iterations; i++) {
			if (bestCost == 0) {
				break;
			}

			// Set move to standard values
			ArrayList<Integer> swapA = new ArrayList<Integer>(); // first row number of possible swaps
			ArrayList<Integer> swapB = new ArrayList<Integer>(); // second row number of possible swaps

			// Try every row swap
			for (int a = 0; a < inst.N - 1; a++) {
				for (int b = a + 1 ; b < inst.N; b++) {
					// Apply the move if possible
					if (sol.applyLocalMove(a, b)) {
						// Get the cost of the solution after the move
						tempCost = sol.getCost();
						// When a move is found that is better than the current best, update it
						if (tempCost == bestCost && a != b) {
							bestCost = tempCost;
							swapA.add(a);
							swapB.add(b);
						} else if (tempCost < bestCost && a != b) {
							bestCost = tempCost;
							swapA.clear();
							swapB.clear();
							swapA.add(a);
							swapB.add(b);
						}
						// Undo the move
						sol.undoLocalMove(a, b);
					}
				}
			}

			// Apply one of the best moves that were found
			if (swapA.size() < 1 || swapB.size() < 1) {
				break;
			}
			solNumber = rand.nextInt(swapA.size());
			sol.applyLocalMove(swapA.get(solNumber), swapB.get(solNumber));
		}
	}
	
}
