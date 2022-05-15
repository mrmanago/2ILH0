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
		double curCost = sol.getSmoothCost();
		int[] tabuList = new int[inst.M];
		int tabuTime = 0;

		// try all local moves
		for (int k = 1; k < nIter; k++) {
			// Print the process of the solver
			if (k % 100 == 0) {
				System.out.println("Completed: " + (float) k/nIter);
				System.out.println("Best sol so far: " + bestSol.getSmoothCost());
			}
			// Clear the solList
			solList.clear();
			// Find the best local move by going through all of them
			for (int s = 0; s < inst.M; s++) {
				// Only allow the move if its not tabu
				if (tabuList[s] < k) {
					sol.swapIngredient(s);
					double newCost = sol.getSmoothCost();
					if (newCost > curCost) {
						curCost = newCost;
						// Empty the solList because there is no tie anymore
						solList.clear();
						// Add tied solutions to the solList
						solList.add(sol.copy());
						// Put the swap on the tabu list
						tabuList[s] = k + tabuTime;
					} else if (newCost == curCost) {	
						// Add tied solutions to the solList
						solList.add(sol.copy());
						// Put the swap on the tabu list
						tabuList[s] = k + tabuTime;
					}

					// Undo the local move
					sol.swapIngredient(s);
				}
			}
			// Use a random solution from the solution list as the new solution
			if (solList.size() <= 0) {
				// There was no better local solution found
				break;
			}
			System.out.println(solList.size());
			bestSol = solList.get(rand.nextInt(solList.size()));
			System.out.println("Cost " + bestSol.getCost());
			System.out.println("Smooth Cost " + bestSol.getSmoothCost());
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
