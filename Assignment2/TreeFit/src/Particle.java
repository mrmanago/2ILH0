import java.util.ArrayList;
import java.util.Random;

public class Particle {

	ParticleSwarmOpt pso; // pointer to optimization parameters
	TFSolution sol; // the corresponding solution of the particle
	ArrayList<Pos> velocity; // velocities of each of the points
	ArrayList<Pos> best; // best solution found by particle
	double bestCost; // cost of the best solution found by this particle
	
	// constructor
	public Particle(TFSolution sol) {
		this.sol = sol;
		velocity = new ArrayList<Pos>();
		best = new ArrayList<Pos>();
		for (int i = 0; i < sol.K; i++) {
			best.add(sol.getPoint(i));
			velocity.add(new Pos(0.0, 0.0));
		}
		bestCost = sol.getCost();
	}
	
	
	// return the current solution
	public TFSolution getSolution() {
		return sol;
	}
	
	
	// return the cost of the best solution found
	public double getBestCost() {
		return bestCost;
	}
	
	
	// clip points to bounding box
	// The velocity becomes 0 if a point hits the boundary of the box, but you may choose a different effect
	public void clipPoint(int i) {
		Pos v = velocity.get(i);
		Pos p = sol.getPoint(i);
		if (p.x < sol.instance.minx) {
			p.x = sol.instance.minx; v.x = 0;
		}
		else if (p.x > sol.instance.maxx) {
			p.x = sol.instance.maxx; v.x = 0;
		}
		if (p.y < sol.instance.miny) {
			p.y = sol.instance.miny; v.y = 0;
		}
		else if (p.y > sol.instance.maxy) {
			p.y = sol.instance.maxy; v.y = 0;
		}	
	}
	
	
	// perform one iteration for this particle and update its personal best solution
	public void update() {
		// compute random numbers
//		Random rand = new Random();
//		int r1 = rand.nextInt(2);
//		int r2 = rand.nextInt(2);


		// for each point
		for (int i = 0; i < sol.K; i++) {
			// compute random numbers
			Random rand = new Random();
			int r1 = rand.nextInt(2);
			int r2 = rand.nextInt(2);

			Pos v = velocity.get(i);
			Pos p = sol.getPoint(i);

			// update velocity
			//System.out.println("Position: " + p.x);
			//System.out.println("Velocity: " + v.x);
			v.x = (pso.cIn * v.x) + (pso.cCog * r1 * (p.x - best.get(i).x)) + (pso.cSoc * r2 * (p.x - pso.globalBest.tree.get(i).x));
			v.y = (pso.cIn * v.y) + (pso.cCog * r1 * (p.y - best.get(i).y)) + (pso.cSoc * r2 * (p.y - pso.globalBest.tree.get(i).y));
			velocity.set(i, new Pos(v.x, v.y));

			// update position based on new velocity
			p.x = p.x + v.x;
			p.y = p.y + v.y;

			sol.setPoint(i, new Pos(p.x, p.y));
			// make sure it doesn't go out of bounds
			clipPoint(i);
		}

		// update particle best
		if (sol.getCost() < bestCost) {
			//System.out.println("found better in particle");
			best.clear();
			for (int i = 0; i < sol.K; i++) {
				Pos p = sol.getPoint(i);
				best.add(new Pos(p.x, p.y));
				bestCost = sol.getCost();
			}
		}
	}
	
	
}
