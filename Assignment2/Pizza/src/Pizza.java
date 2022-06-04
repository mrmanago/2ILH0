import java.util.Random;
import java.util.spi.CurrencyNameProvider;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

final class AntRank implements Comparable<AntRank> {
	double cost;
	int index;

	AntRank(double cost, int index) {
		this.cost = cost;
		this.index = index;
	}

	@Override
	public int compareTo(AntRank ant) {
		if (this.cost < ant.cost) {
			return -1;
		} else if (this.cost == ant.cost) {
			return 0;
		} else {
			return 1;
		}
	}
}

final class solutionRank implements Comparable<solutionRank> {
	double cost;
	PizzaSolution sol;

	solutionRank(double cost, PizzaSolution sol) {
		this.cost = cost;
		this.sol = sol;
	}

	@Override
	public int compareTo(solutionRank sol) {
		if (this.cost < sol.cost) {
			return -1;
		} else if (this.cost == sol.cost) {
			return 0;
		} else {
			return 1;
		}
	}
}

public class Pizza {

	static Random random = new Random();
	static final String dir = System.getProperty("user.dir") + "/Pizza/";
	static final String dataset = "difficult"; // choose the dataset

	public static void main(String[] args) {
		//System.out.println("Directory: " + dir);
		PizzaInstance inst = new PizzaInstance(dir + "data/" + dataset + ".txt"); // load the problem instance
		PizzaSolution sol = new PizzaSolution(inst, true); // initialize a (random) solution
		sol.computeGreedy(); // run the original greedy algorithm
		//AntColonyOpt ants = new AntColonyOpt(inst.M, 2, 6, ((double) 1/inst.M), 0.5, 1); // make object for Ant Colony Optimization
		//sol.computeGreedy(ants); // run the ant colony greedy algorithm
		//PizzaSolution sol = antColony(inst, 25, 50, ants); // perform ant colony optimization
		//PizzaSolution sol = antColonyRank(inst, 25, 50, ants); // perform ant colony optimization with rank-based pheromone depositing
		//PizzaSolution sol = geneticAlg(inst, 200, 300, 50); // perform the genetic algorithm
		System.out.println("Cost = " + sol.getCost()); // output the cost
		sol.output(dir + "output/" + dataset + ".out"); // output the solution
		sol.visualize(dir + "figures/" + dataset + ".png", true); // visualize the solution
	}	
	
	// perform ant colony optimization with [nAnts] ants and [nIter] iterations/rounds
	public static PizzaSolution antColony(PizzaInstance inst, int nAnts, int nIter, AntColonyOpt antOpt) {
		PizzaSolution bestSol = new PizzaSolution(inst, true);
		double bestCost = 1;

		// Create nAnts solutions (ant) using computeGreedy(ants) where ants is the context.
		// After creating the solutions update the context (ants).
		// Do this for nIter iterations and keep track of the best solution.
		for (int iter=0; iter<nIter; iter++) {
			// Print progress
			//System.out.println("   (" + (double) iter/nIter + ")");

			// Initialize new ants
			PizzaSolution[] ants = new PizzaSolution[nAnts];
			for (int a=0; a<ants.length; a++) {
				ants[a] = new PizzaSolution(inst, true);
			}

			antOpt.initRound();

			// Let the ants find solutions
			for (int s=0; s<nAnts; s++) {
				//System.out.println("      (" + (double) s/nAnts + ")");
				ants[s].computeGreedy(antOpt);
			}

			// Add the pheromone from the ants
			for (int s=0; s<nAnts; s++) {
				// Check if the solution is better than the current best
				if (ants[s].getCost() > bestCost) {
					// Change best solution
					bestCost = ants[s].getCost();
					bestSol = ants[s].copy();
					// Print that solution has been improved
					System.out.println("Solution improved to: " + bestCost);
				}

				// For every ingredient update the pheromone levels of the choice that was made in the solution
				if (ants[s].getCost() > 0) {
					ants[s].addPheromones(antOpt, (ants[s].getCost() / bestCost)*(antOpt.Q));
				}
			}

			// Print the pheromone levels
			// if (iter % 10 == 0) {
			// 	System.out.println("Pher array");
			// 	System.out.println(Arrays.deepToString(antOpt.pher));
			// 	System.out.println("Delta pher array");
			// 	System.out.println(Arrays.deepToString(antOpt.deltaPher));
			// }

			// Conclude the round
			antOpt.concludeRound();

			// Print best cost after this round
			//System.out.println("Current best cost: " + bestCost);
		}

		return bestSol;
	}
	
	
	
	// perform ant colony optimization with [nAnts] ants and [nIter] iterations/rounds and rank-based pheromone depositing
	public static PizzaSolution antColonyRank(PizzaInstance inst, int nAnts, int nIter, AntColonyOpt antOpt) {	
		PizzaSolution bestSol = new PizzaSolution(inst, true);
		double bestCost = 1;

		// Create nAnts solutions (ant) using computeGreedy(ants) where ants is the context.
		// After creating the solutions update the context (ants).
		// Do this for nIter iterations and keep track of the best solution.
		for (int iter=0; iter<nIter; iter++) {
			// Print progress
			//System.out.println("   (" + (double) iter/nIter + ")");

			// Initialize new ants
			PizzaSolution[] ants = new PizzaSolution[nAnts];
			for (int s=0; s<ants.length; s++) {
				ants[s] = new PizzaSolution(inst, true);
			}

			antOpt.initRound();

			// Let the ants find solutions
			for (int s=0; s<nAnts; s++) {
				//System.out.println("      (" + (double) s/nAnts + ")");
				ants[s].computeGreedy(antOpt);
			}

			// Create ranking objects for the ants
			AntRank[] ranking = new AntRank[nAnts];

			// Populate the ranking array
			for (int s=0; s<ants.length; s++){
				double cost = ants[s].getCost();
				int index = s;
				ranking[s] = new AntRank(cost, index);
			}
			
			// Sort the ants (ascending order)
			Arrays.sort(ranking);

			// Add the pheromone from the ants
			for (int s=0; s<nAnts; s++) {
				// Iterate over the solutions in ascending order
				PizzaSolution ant = ants[ranking[s].index];
				// Check if the solution is better than the current best
				if (ant.getCost() > bestCost) {
					// Change best solution
					bestCost = ant.getCost();
					bestSol = ant.copy();
					// Print that solution has been improved
					System.out.println("Solution improved to: " + bestCost);
				}

				// For every ingredient update the pheromone levels of the choice that was made in the solution
				if (ant.getCost() > 0) {
					ant.addPheromones(antOpt, ((double) (s+1) /  ((double) nAnts )) * (antOpt.Q));
				}
			}

			// // Print the pheromone levels
			// if (iter % 10 == 0) {
			// 	System.out.println("Pher array");
			// 	System.out.println(Arrays.deepToString(antOpt.pher));
			// 	System.out.println("Delta pher array");
			// 	System.out.println(Arrays.deepToString(antOpt.deltaPher));
			// }

			// Conclude the round
			antOpt.concludeRound();

			// Print best cost after this round
			//System.out.println("Current best cost: " + bestCost);
		}
		
		return bestSol;
	}
	
	
	
	
	// perform genetic algorithm with [select] solutions after selection, and [offSpring] solutions after crossovers/mutations
	// The number of iterations/generations must be [nIter]
	// You may choose the type of selection yourself
	public static PizzaSolution geneticAlg(PizzaInstance inst, int select, int offSpring, int nIter) {
		PizzaSolution bestSol = new PizzaSolution(inst, true);
		double bestCost = bestSol.getCost();
		int eliteAmount = Math.round((float) select / (float) 2);

		ArrayList<solutionRank> currentGen = new ArrayList<>();
		ArrayList<solutionRank> nextGen = new ArrayList<>();
		ArrayList<solutionRank> selection = new ArrayList<>();

		// Initialize current generation
		for (int s=0; s<select; s++) {
			PizzaSolution sol = new PizzaSolution(inst, true);
			sol.computeGreedy();
			currentGen.add(new solutionRank(sol.getCost(), sol));
		}

		// Genetic algorithm loop
		for (int iter=0; iter < nIter; iter++) {
			// Print progress
			if (iter % 10 == 0) {
				System.out.println("Progress: " + (double) iter/nIter);
			}

			// Sort the solutions in the current generation
			Collections.sort(currentGen);

			// Store x best solutions (elitism)
			for (int s=0; s<eliteAmount; s++) {
				nextGen.add(currentGen.get(select - s - 1));
			}

			// Store selected solutions (selection)
			for (int s=0; s<select; s++) {
				selection.add(currentGen.get(select - s - 1));
			}

			// Randomly apply crossover to selected solutions
			for (int s=0; s<offSpring; s++) {
				int index1 = random.nextInt(select - 1);
				int index2 = random.nextInt(select - 1);
				if (index1 == index2) {
					if (index1 < select - 2) {
						index1++;
					} else {
						index1 --;
					}
				}
				PizzaSolution sol = PizzaSolution.crossover(selection.get(index1).sol, selection.get(index2).sol);
				double solCost = sol.getCost();
				// Check if crossover is better or equal and if so replace, or replace by greedy with p=0.2
				if (solCost >= selection.get(index1).cost) {
					selection.get(index1).cost = solCost;
					selection.get(index1).sol = sol.copy();
					if (selection.get(index1).cost > bestCost) {
						bestSol = selection.get(index1).sol.copy();
						bestCost = selection.get(index1).sol.getCost();
					}
				} else if (Math.random() < 0.1) {
					selection.get(index1).sol.computeGreedy();
					bestSol = selection.get(index1).sol.copy();
					bestCost = selection.get(index1).sol.getCost();
				}
				// Check if crossover is better or equal and if so replace, or replace by greedy with p=0.2
				if (solCost >= selection.get(index1).cost) {
					selection.get(index2).cost = solCost;
					selection.get(index2).sol = sol.copy();
					if (selection.get(index2).cost > bestCost) {
						bestSol = selection.get(index2).sol.copy();
						bestCost = selection.get(index2).sol.getCost();
					}
				} else if (Math.random() < 0.1) {
					selection.get(index2).sol.computeGreedy();
					bestSol = selection.get(index2).sol.copy();
					bestCost = selection.get(index2).sol.getCost();
				}
			}

			// Apply mutation to all selected solutions
			for (int s=0; s<select; s++) {
				selection.get(s).sol.mutate();
				selection.get(s).cost = selection.get(s).sol.getCost();
				if (selection.get(s).cost > bestCost) {
					bestSol = selection.get(s).sol.copy();
					bestCost = selection.get(s).sol.getCost();
				}
			}

			// Sort the selected solutions
			Collections.sort(selection);

			// Put the best (adapated) selected solutions in nextGen
			for (int s=0; s<(select-eliteAmount); s++) {
				nextGen.add(selection.get(select - s - 1));
			}

			// Replace current gen by next gen
			for (int i=0; i<select; i++) {
				currentGen.get(i).cost = nextGen.get(i).cost;
				currentGen.get(i).sol = nextGen.get(i).sol;
			}

			// Clear the next gen
			nextGen.clear();
		}
		
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