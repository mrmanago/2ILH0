import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

final class solutionRep {
	final double cost;
	final String params;

	solutionRep(double cost, String params) {
		this.cost = cost;
		this.params = params;
	}
}

final class solutionRepComparator implements Comparator<solutionRep> {
    @Override
    public int compare(solutionRep rep1, solutionRep rep2) {
		if (rep1.cost < rep2.cost) {
			return -1;
		} else if (rep1.cost == rep2.cost) {
			return 0;
		} else {
			return 1;
		}
    }
}

public class Pizza {

	// Local variables to save results and parameters
	static ArrayList<solutionRep> sols = new ArrayList<>();
	static Random random = new Random();

	public static void main(String[] args) {
		String dataset = "small"; // choose the dataset
		String dir = System.getProperty("user.dir") + "/Assignment2/Pizza/";
		//System.out.println("Directory: " + dir);
		PizzaInstance inst = new PizzaInstance(dir + "data/" + dataset + ".txt"); // load the problem instance
		//PizzaSolution sol = new PizzaSolution(inst, true); // initialize a (random) solution
		//sol.computeGreedy(); // run the original greedy algorithm
		//AntColonyOpt ants = new AntColonyOpt(inst.M, 1, 1, 0.5, 0.75, 0.1); // make object for Ant Colony Optimization
		//sol.computeGreedy(ants); // run the ant colony greedy algorithm
		//PizzaSolution sol = antColony(inst, 5, 1000, ants); // perform ant colony optimization
		tuneParameters(inst, 1000);
		//PizzaSolution sol = antColonyRank(inst, ..., ..., ants); // perform ant colony optimization with rank-based pheromone depositing
		//PizzaSolution sol = geneticAlg(inst, ..., ..., ...); // perform the genetic algorithm
		//System.out.println("Cost = " + sol.getCost()); // output the cost
		//sol.output(dir + "output/" + dataset + ".out"); // output the solution
		//sol.visualize(dir + "figures/" + dataset + ".png", true); // visualize the solution
	}

	// Tune parameters by trying many configurations
	public static void tuneParameters(PizzaInstance inst, int iterations) {
		// Options for parameters
		int[] ants = {5, 10, 15, 20, 25};
		double[] alphas = {0.1, 0.3, 0.5, 1, 1.5, 2};
		double[] betas = {0.1, 0.3, 0.5, 1, 1.5, 2};
		double[] qs = {0.5, 1, 2, 3, 4};
		double[] rhos = {0.2, 0.4, 0.5, 0.6, 0.8, 0.9};
		// Run random settings
		for(int i=0; i<iterations; i++) {
			AntColonyOpt colony = new AntColonyOpt(
				inst.M, alphas[random.nextInt(alphas.length)], 
				betas[random.nextInt(betas.length)], 
				qs[random.nextInt(qs.length)], 
				rhos[random.nextInt(rhos.length)], 
				0.1
				);
			int antAmount = ants[random.nextInt(ants.length)];
			PizzaSolution sol = antColony(inst, antAmount, 10, colony);
			sols.add(new solutionRep(sol.getCost(), "ants: " + antAmount + " alpha: " + colony.alpha +
				" beta: " + colony.beta + " Q: " + colony.Q + " rho: " + colony.rho));			
		}
		Collections.sort(sols, new solutionRepComparator());
		// Print n best solutions
		int n = 20;
		for (int i=0; i<n; i++) {
			System.out.println("Cost: " + sols.get(iterations-i-1).cost + ", using: " + sols.get(i).params);
		}
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
					// System.out.println("Solution improved to: " + bestCost);
				}

				// For every ingredient update the pheromone levels of the choice that was made in the solution
				for (int i=0; i<inst.M; i++) {
					if (ants[s].onPizza[i]) {
						antOpt.addPheromone(i, 1, (ants[s].getCost() / bestCost)*(antOpt.Q / ants[s].getNrOnPizza()));
					} else {
						antOpt.addPheromone(i, 0, (ants[s].getCost() / bestCost)*(antOpt.Q / ants[s].getNrOnPizza()));
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