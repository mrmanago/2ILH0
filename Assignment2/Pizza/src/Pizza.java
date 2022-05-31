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

public class Pizza {

	static Random random = new Random();
	static final String dir = System.getProperty("user.dir") + "/";
	static final String dataset = "medium"; // choose the dataset

	public static void main(String[] args) {
		//System.out.println("Directory: " + dir);
		PizzaInstance inst = new PizzaInstance(dir + "data/" + dataset + ".txt"); // load the problem instance
		//PizzaSolution sol = new PizzaSolution(inst, true); // initialize a (random) solution
		//sol.computeGreedy(); // run the original greedy algorithm
		//AntColonyOpt ants = new AntColonyOpt(inst.M, 2, 6, (double) 1/inst.M, 0.5, 1); // make object for Ant Colony Optimization
		//sol.computeGreedy(ants); // run the ant colony greedy algorithm
		//PizzaSolution sol = antColony(inst, 15, 50, ants); // perform ant colony optimization
		//PizzaSolution sol = antColonyRank(inst, 15, 40, ants); // perform ant colony optimization with rank-based pheromone depositing
		PizzaSolution sol = geneticAlg(inst, 30, 50, 1000); // perform the genetic algorithm
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

			// TODO: What is this for?
			antOpt.initRound();

			// Let the ants find solutions
			for (int s=0; s<nAnts; s++) {
				//System.out.println("      (" + (double) s/nAnts + ")");
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
				if (ants[s].getCost() > 0) {
					ants[s].addPheromones(antOpt, (ants[s].getCost() / bestCost)*(antOpt.Q));
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
	
	
	
	// perform ant colony optimization with [nAnts] ants and [nIter] iterations/rounds and rank-based pheromone depositing
	public static PizzaSolution antColonyRank(PizzaInstance inst, int nAnts, int nIter, AntColonyOpt antOpt) {	
		PizzaSolution bestSol = new PizzaSolution(inst, true);
		double bestCost = 1;
		
		// TODO

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

			// TODO: What is this for?
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
				// TODO: Should we do something here for equality?
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
		int eliteAmount = Math.round((float) select / (float) 10);
		double crossoverProb = 0.5;

		// TODO
		// Create current generation array
		PizzaSolution[] currentGen = new PizzaSolution[select];
		// Create elite array
		PizzaSolution[] eliteGen = new PizzaSolution[eliteAmount];
		// Create arrays for the selection before and after crossover/mutation
		PizzaSolution[] selectGenOld = new PizzaSolution[select];
		PizzaSolution[] selectGenNew = new PizzaSolution[offSpring];
		// Initialize current generation
		for (int s=0; s<select; s++) {
			currentGen[s] = new PizzaSolution(inst, true);
		}

		// Genetic algorithm loop
		for (int iter=0; iter < nIter; iter++) {
			// Sort the solutions in the current generation
			// 		Create ranking objects for the currentGen
			AntRank[] ranking = new AntRank[currentGen.length];

			// 		Populate the ranking array
			for (int s=0; s<currentGen.length; s++) {
				double cost = currentGen[s].getCost();
				// Check the cost and update best solution
				if (cost > bestCost) {
					bestCost = cost;
					bestSol = currentGen[s].copy();
					System.out.println("Best solution improved to: " + bestCost);
				}
				int index = s;
				ranking[s] = new AntRank(cost, index);
			}

			// 		Sort the ranking
			Arrays.sort(ranking);

			// Store x best solutions (elitism)
			for (int s=0; s<eliteAmount; s++) {
				eliteGen[s] = currentGen[ranking[currentGen.length - s - 1].index];
			}

			// Store select selected solitions (selection)
			for (int s=0; s<select; s++) {
				selectGenOld[s] = currentGen[ranking[currentGen.length - s - 1].index];
			}

			// Randomly apply crossover to selected solutions and store in selectGenNew
			for (int s=0; s<selectGenOld.length; s++) {
				if (Math.random() < crossoverProb) {
					// Find a random solution other than s itself
					int randomIndex = random.nextInt(selectGenOld.length);
					if (randomIndex == s) {
						if (s + 1 < selectGenOld.length - 1) {
							randomIndex += 1;
						} else {
							randomIndex -= 1;
						}
					}
					// Apply crossover
					selectGenNew[s] = PizzaSolution.crossover(selectGenOld[s], selectGenOld[randomIndex]);
				} else {
					// Leave the solution as is
					selectGenNew[s] = selectGenOld[s];
				}
			}

			// Randomly apply mutations to selectGenNew
			for (int s=0; s<(offSpring - select); s++) {
				// Get a random index
				int index = random.nextInt(selectGenOld.length);
				// Copy the solution
				PizzaSolution sol = selectGenOld[index];
				// Apply mutation
				sol.mutate();
				// Add the mutated solution to selectGenNew
				selectGenNew[selectGenOld.length + s] = sol;
			}

			// Sort the best (select - x) solutions and combine with the eliteGen into a new currentGen
			// 		Create ranking objects for the nextGen
			ranking = new AntRank[selectGenNew.length];

			// 		Populate the ranking array
			for (int s=0; s<selectGenNew.length; s++) {
				double cost = selectGenNew[s].getCost();
				int index = s;
				ranking[s] = new AntRank(cost, index);
			}

			// 		Sort the ranking
			Arrays.sort(ranking);

			// Put back the x best solutions (elitism)
			for (int s=0; s<eliteAmount; s++) {
				currentGen[s] = eliteGen[s];
			}

			// Add (currentGen.length - x) best solutions from nextGen to currentGen
			for (int s=eliteAmount; s<currentGen.length; s++) {
				currentGen[s] = selectGenNew[ranking[selectGenNew.length - (s-eliteAmount) - 1].index];
			}
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