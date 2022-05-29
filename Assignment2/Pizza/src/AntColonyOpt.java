
public class AntColonyOpt {
	
	double[][] pher; // current pheromones -- first index is ingredient, second index is 0 (add ingredient) or 1 (exclude ingredient)
	double[][] deltaPher; // change in pheromones for current round
	double alpha, beta, Q, rho; // parameters for ant colony optimization
	int M; // number of ingredients
	// you can add other parameters for Ant Colony System yourself
	
	// In this constructor, [initPher] is the initial pheromone level for each pair of cities
	public AntColonyOpt(int M, double alpha, double beta, double Q, double rho, double initPher) {
		this.M = M;
		this.alpha = alpha;
		this.beta = beta;
		this.Q = Q;
		this.rho = rho;
		pher = new double[M][2];
		deltaPher = new double[M][2];
		for (int i = 0; i < M; i++) {
			pher[i][0] = initPher; // initialize pheromone levels
			pher[i][1] = initPher;
		}
	}
	
	
	// initalize a round (iteration) of the ant colony optimization algorithm
	public void initRound() {
		// TODO
	}
	
	
	// add pheromone to a greedy choice of adding/omitting an ingredient
	public void addPheromone(int i, int val, double amount) {
		deltaPher[i][val] += amount;		
	}
	
	
	// compute the preference for adding/omitting an ingredient
	public double getPreference(int i, int val, double visibility) {
		return Math.pow(pher[i][val], alpha) * Math.pow(visibility, beta);
	}
	
	
	// conclude a round (iteration) of the ant colony optimization algorithm
	public void concludeRound() {
		// update the pheromone levels
		for (int i=0; i<M; i++) {
			for (int j=0; j<2; j++) {
				pher[i][j] = (1-rho)*pher[i][j] + deltaPher[i][j]; 
			}
		}
	}
	
}
