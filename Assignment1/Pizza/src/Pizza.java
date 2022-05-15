import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeSet;

public class Pizza {

	public static void main(String[] args) {
		String dataset = "medium"; // choose the dataset
		PizzaInstance inst = new PizzaInstance("data/" + dataset + ".txt"); // load the problem instance
		PizzaSolution sol = new PizzaSolution(inst, false); // initialize a (random) solution
		//randomIterativeImprovement(inst, sol, 100000); // perform iterative improvement
		sol = tabuSearch(inst, sol, 100); // perform tabu search
		//simAnnealing(inst, sol, 1000000); // perform simulated annealing
		//bestSimAnnealing(inst, sol, 1000000); // perform improved simulated annealing
		System.out.println("Cost = " + sol.getCost()); // output the cost
		sol.output("output/" + dataset + ".out"); // output the solution
		sol.visualize("figures/" + dataset + ".png", true); // visualize the solution
	}

	
	
	
	// perform random iterative improvement for [nIter] iterations (solution will be changed!)
	public static void randomIterativeImprovement(PizzaInstance inst, PizzaSolution sol, int nIter) {
		
		// initialize
		double curCost = sol.getSmoothCost();
		Random rand = new Random();

		// keep applying local move
		for (int k = 0; k < nIter; k++) {
			int a = rand.nextInt(inst.M);
			sol.swapIngredient(a);
			double newCost = sol.getSmoothCost();
			if (newCost > curCost) {
				curCost = newCost;
			}
			else {
				sol.swapIngredient(a);
			}
		}
	}
	
	
	
	
	// Execute a tabu search, where [nIter] is the number of iterations; must return the best found solution
	// Solutions should be copied using the PizzaSolution.copy() function!
	public static PizzaSolution tabuSearch(PizzaInstance inst, PizzaSolution sol, int nIter) {
		
		// initialize
		Random rand = new Random();
		PizzaSolution bestSol = sol.copy();
		ArrayList<PizzaSolution> solList = new ArrayList<>();
		ArrayList<Integer> moveList = new ArrayList<>();
		double curCost = sol.getSmoothCost();
		double bestCost = sol.getCost();
		int[] tabuList = new int[inst.M];
		int tabuTime = 5;

		// Improve solution iteratively
		for (int k = 1; k < nIter; k++) {
			// Print the process of the solver
			if (k % 100 == 0) {
				System.out.println("Completed: " + (float) k/nIter);
				System.out.println("Best sol so far: " + bestSol.getCost());
			}
			// Clear the solList
			solList.clear();
			// Find the best local move by going through all of them
			for (int s = 0; s < inst.M; s++) {
				// Only consider the move if its not on the tabu list
				if (tabuList[s] < k) {
					// Execute local move
					sol.swapIngredient(s);
					// Get cost of new solution
					double newCost = sol.getSmoothCost();
					// If new solution is better than best
					if (sol.getCost() > bestCost) {
						System.out.println("Found better cost " + sol.getCost());
						bestSol = sol.copy();
						bestCost = sol.getCost();
					}
					// If new solution is better than current
					if (newCost > curCost) {
						curCost = newCost;
						// Empty the solList because there is no tie anymore
						solList.clear();
						moveList.clear();
						// Add solution to the solList and move to moveList
						solList.add(sol.copy());
						moveList.add(s);
					} else if (newCost == curCost) {	
						// Add tied solutions to the solList and move to moveList
						solList.add(sol.copy());
						moveList.add(s);
					}
					// Undo the local move
					sol.swapIngredient(s);
				}
			}
			// If no better solution was found take a random next step (including possible tabu move)
			if (solList.size() <= 0) {
				// There was no better local solution found
				sol.swapIngredient(rand.nextInt(inst.M));
			} else {
				// Use a random best solution as the new solution and add the used move to the tabu list
				int randomIndex = rand.nextInt(solList.size());
				sol = solList.get(randomIndex);
				tabuList[moveList.get(randomIndex)] = k + tabuTime;
			}
		}
		return bestSol;
	}

	
	
	
	
	// perform simulated annealing, where [nIter] is the number of iterations
	// you may add more parameters if you wish (e.g. temperature settings, etc.)
	public static void simAnnealing(PizzaInstance inst, PizzaSolution sol, int nIter) {
		
		// TODO
		
	}
	
	
	
	// perform improved (!) simulated annealing, where [nIter] is the number of iterations
	// you may add more parameters if you wish (e.g. temperature settings, etc.)
	public static void bestSimAnnealing(PizzaInstance inst, PizzaSolution sol, int nIter) {
		
		// TODO
		
	}
	
	
	
}
