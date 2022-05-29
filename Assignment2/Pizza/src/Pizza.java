import java.util.Random;

public class Pizza {

	public static void main(String[] args) {
		String dataset = "small"; // choose the dataset
		String dir = System.getProperty("user.dir") + "/Pizza/";
		PizzaInstance inst = new PizzaInstance(dir + "data/" + dataset + ".txt"); // load the problem instance
		//PizzaSolution sol = new PizzaSolution(inst, true); // initialize a (random) solution
		//sol.computeGreedy(); // run the original greedy algorithm
		AntColonyOpt ants = new AntColonyOpt(inst.M, 1, 1, 0.5, 3/4, 1); // make object for Ant Colony Optimization
		//sol.computeGreedy(ants); // run the ant colony greedy algorithm
		PizzaSolution sol = antColony(inst, 5, 1000, ants); // perform ant colony optimization
		//PizzaSolution sol = antColonyRank(inst, ..., ..., ants); // perform ant colony optimization with rank-based pheromone depositing
		//PizzaSolution sol = geneticAlg(inst, ..., ..., ...); // perform the genetic algorithm
		System.out.println("Cost = " + sol.getCost()); // output the cost
		sol.output(dir + "output/" + dataset + ".out"); // output the solution
		sol.visualize(dir + "figures/" + dataset + ".png", true); // visualize the solution
	}

	
	
	
	// perform ant colony optimization with [nAnts] ants and [nIter] iterations/rounds
	public static PizzaSolution antColony(PizzaInstance inst, int nAnts, int nIter, AntColonyOpt antOpt) {
		
		PizzaSolution bestSol = new PizzaSolution(inst, true);
		double bestCost = bestSol.getCost();
		
		// TODO

		// Create nAnts solutions (ant) using computeGreedy(ants) where ants is the context.
		// After creating the solutions update the context (ants).
		// Do this for nIter iterations and keep track of the best solution.
		for (int iter=0; iter<nIter; iter++) {
			// Initialize new ants
			PizzaSolution[] ants = new PizzaSolution[nAnts];
			for (int a=0; a<ants.length; a++) {
				ants[a] = new PizzaSolution(inst, true);
			}

			// TODO: What is this for?
			antOpt.initRound();

			// Let the ants find solutions
			for (int s=0; s<nAnts; s++) {
				ants[s].computeGreedy(antOpt);
			}

			// Add the pheromone from the ants
			for (int s=0; s<nAnts; s++) {
				// Check if the solution is better than the current best
				// TODO: Should we do something here for equality?
				if (ants[s].getCost() > bestCost) {
					// Change best solution
					bestCost = ants[s].getCost();
					bestSol = ants[s].copy();
					// Print that solution has been improved
					System.out.println("Solution improved to: " + bestCost);
				}

				// For every ingredient update the pheromone levels of the choice that was made in the solution
				for (int i=0; i<inst.M; i++) {
					if (ants[s].onPizza[i]) {
						antOpt.addPheromone(i, 1, antOpt.Q / ants[s].getNrOnPizza());
					} else {
						antOpt.addPheromone(i, 0, antOpt.Q / ants[s].getNrOnPizza());
					}
				}
			}

			// Conclude the round
			antOpt.concludeRound();
		}

		return bestSol;
	}
	
	
	
	// perform ant colony optimization with [nAnts] ants and [nIter] iterations/rounds and rank-based pheromone depositing
	public static PizzaSolution antColonyRank(PizzaInstance inst, int nAnts, int nIter, AntColonyOpt ants) {
		
		PizzaSolution bestSol = new PizzaSolution(inst, true);
		
		// TODO
		
		return bestSol;
	}
	
	
	
	
	// perform genetic algorithm with [select] solutions after selection, and [offSpring] solutions after crossovers/mutations
	// The number of iterations/generations must be [nIter]
	// You may choose the type of selection yourself
	public static PizzaSolution geneticAlg(PizzaInstance inst, int select, int offSpring, int nIter) {
		
		PizzaSolution bestSol = new PizzaSolution(inst, true);
		
		// TODO
		
		return bestSol;
	}
	
	
	
	// perform random iterative improvement for [nIter] iterations (solution will be changed!)
	public static void randomIterativeImprovement(PizzaInstance inst, PizzaSolution sol, int nIter) {
		
		// initialize
		double curCost = sol.getCost();
		Random rand = new Random();

		// keep applying local move
		for (int k = 0; k < nIter; k++) {
			int a = rand.nextInt(inst.M);
			sol.swapIngredient(a);
			double newCost = sol.getCost();
			if (newCost > curCost) {
				curCost = newCost;
			}
			else {
				sol.swapIngredient(a);
			}
		}

	}
	
	
}