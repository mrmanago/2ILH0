import java.util.ArrayList;

public class NQueens {

	public static void main(String[] args) {
		String dataset = "8"; // choose the dataset
		NQueenInstance inst = new NQueenInstance("data/" + dataset + ".txt"); // load the problem instance
		//NQBasicSol sol = new NQBasicSol(inst, true); // initialize a (random) solution
		NQPermSol sol = new NQPermSol(inst, true); // initialize a (random) solution
		bestIterativeImprovement(inst, sol);
		System.out.println("Cost = " + sol.getCost());
		sol.visualize("figures/" + dataset + ".png");
		sol.output("output/" + dataset + ".txt");
	}
	
	

	// perform best iterative improvement (solution should be changed directly)
	public static void bestIterativeImprovement(NQueenInstance inst, NQBasicSol sol) {

		while (true) {
			// Arraylist to store all possible moves
			ArrayList<int[]> neighbors = new ArrayList<int[]>();
			
			// Current cost
			int currCost = sol.getCost();

			// For each queen
			for (int i = 0; i < sol.N; i++) {
				// For each possible move
				for (int j = 0; j < 8; j++) {
					if (sol.applyLocalMove(i, j)) {
						neighbors.add(new int[]{sol.getCost(), i, j});
						sol.undoLocalMove(i, j);
					}
				}
			}

			System.out.println("Cost = " + sol.getCost());

			// find smallest cost move
			int[] smallest = neighbors.get(0);
			for (int i = 1; i < neighbors.size(); i++) {
				if(neighbors.get(i)[0] < smallest[0]) {
					smallest = neighbors.get(i);
				}
			}

			// If the best move in the array is lower than the current board, execute move, otherwise no more improvement
			if (smallest[0] < currCost) {
				sol.applyLocalMove(smallest[1],smallest[2]);
			} else {
				break;
			}
		}
	}
	
	

	// perform best iterative improvement (solution should be changed directly)
	public static void bestIterativeImprovement(NQueenInstance inst, NQPermSol sol) {
		
		while (true) {
			ArrayList<int[]> neighbors = new ArrayList<int[]>();

			int currCost = sol.getCost();

			// for each row swap
			for (int i = 0; i < inst.N; i++) {
				for (int j = 0; j < inst.N; j++) {
					sol.applyLocalMove(i, j, 1);
					neighbors.add(new int[]{sol.getCost(), i, j, 1});
					sol.undoLocalMove(i, j, 1);
				}
			}

//			// for each column swap. OBSOLETE. Only switch either ror OR column
//			for (int i = 0; i < inst.N; i++) {
//				for (int j = 0; j < inst.N; j++) {
//					sol.applyLocalMove(i, j, 0);
//					neighbors.add(new int[]{sol.getCost(), i, j, 0});
//					sol.undoLocalMove(i, j, 0);
//				}
//			}

			// find the lowest cost
			int[] smallest = neighbors.get(0);
			for (int i = 1; i < neighbors.size(); i++) {
				if (neighbors.get(i)[0] < smallest[0]) {
					smallest = neighbors.get(i);
				}
			}

			if (smallest[0] < currCost) {
				sol.applyLocalMove(smallest[1], smallest[2], smallest[3]);
			} else {
				break;
			}
		}
		
	}
	
}
