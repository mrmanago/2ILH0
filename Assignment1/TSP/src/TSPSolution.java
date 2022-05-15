import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class TSPSolution {
	
	TSPInstance instance; // instance
	ArrayList<Integer> perm; // solution representation (perm[i] is i-th point in tour)
	int N; // a shorthand for the number of points

	boolean random; // if the solution is calculated at random or not
	
	// constructor
	public TSPSolution(TSPInstance inst, boolean random) {
		instance = inst;
		N = instance.N;
		this.random = random;
		
		// initialize with the standard order
		perm = new ArrayList<Integer>();
		for (int i = 0; i < N; i++) {
			perm.add(i);
		}
		
		if (random) {
			// randomize order
			Random rand = new Random();
			for (int i = 0; i < N-1; i++) {
				int k = rand.nextInt(N-i) + i;
				Collections.swap(perm, i, k);
			}
		}
		
	}

	
	// copy the solution (if you want to keep track of the best solution found)
	public TSPSolution copy() {
		TSPSolution sol = new TSPSolution(instance, false);
		for (int i = 0; i < N; i++) {
			sol.perm.set(i, perm.get(i));
		}
		return sol;
	}
	
	
	
	// greedy algorithm that starts forming a tour with two random points and repeatedly inserts a random point in the best possible place in the tour
	public void computeGreedy() {

		// pick 2 random points. make a tour.
		// for remaining points
		//	pick random point
		//	for each possible place to put in tour
		//  	calc cost to put in tour
		// 	insert point where lowest cost

		ArrayList<Integer> tour = new ArrayList<Integer>();

		// Random order of points to be inserted set
		ArrayList<Integer> orderOfPoints = new ArrayList<Integer>();
		for (int i = 0; i < N; i++) {
			orderOfPoints.add(i);
		}
		if (random) {
			Collections.shuffle(orderOfPoints);
		}

		// perm is equal to {1, 2, 1, 1, etc}
		tour.add(orderOfPoints.get(0));
		tour.add(orderOfPoints.get(1));
		makeSubTour(tour);

		// for each remaining point
		for (int i = 2; i < N; i++) {
			ArrayList<double[]> positionCost = new ArrayList<>();
			// for each possible insert. tour size + 1
			for (int j = 0; j <= tour.size(); j++) {
				tour.add(j, orderOfPoints.get(i));
				makeSubTour(tour);
				positionCost.add(new double[]{getCost(), (double) j});
				tour.remove(j);
			}

			// Find where best to insert the point
			double[] smallest = positionCost.get(0);
			for (int j = 1; j < positionCost.size(); j++) {
				if (positionCost.get(j)[0] < smallest[0]) {
					smallest = positionCost.get(j);
				}
			}

			// Insert best point
			tour.add((int) smallest[1], orderOfPoints.get(i));
			makeSubTour(tour);
		}
	}

	// changes perm variable to smaller tour for greedy solution so cost can be calculated
	public void makeSubTour(ArrayList<Integer> tour) {
		for (int i = 0; i < N; i++) {
			if (i < tour.size()) {
				perm.set(i, tour.get(i));
			} else {
				perm.set(i, tour.get(0));
			}
		}
	}
	
	
	// performs 2-opt move on i-th and j-th edge in tour
	public void apply2OPT(int i, int j) {
		
		// TODO
		
	}
	
	public void undo2OPT(int i, int j) {
		apply2OPT(i, j);
	}	
	
	
	
	// swaps the current i-th and j-th point in the tour
	public void applySwap(int i, int j) {
		Collections.swap(perm, i, j);
	}
	
	public void undoSwap(int i, int j) {
		applySwap(i, j);
	}
	
	
	
	// get the ID of the i^th point in the tour
	public int getPoint(int i) {
		return perm.get(i);
	}
	
	
	
	// compute the cost of the solution
	public double getCost() {
		// simply add distances of edges
		double cost = 0.0;
		for (int i = 0; i < N; i++) {
			int j = (i+1)%N;
			cost += Pos.distance(instance.points.get(perm.get(i)), instance.points.get(perm.get(j)));
		}
		return cost;
	}
	
	
	
	// -------------------------------- OUTPUT STUFF ----------------------------------------------------
	
	
	
	// visualize solution as ipe file
	public void visualize(String filename) {
		
		// first determine bounding box for coordinates
		double minx = Double.MAX_VALUE;
		double miny = Double.MAX_VALUE;
		double maxx = Double.MIN_VALUE;
		double maxy = Double.MIN_VALUE;
		
		for (int i = 0; i < N; i++) {
			minx = Math.min(minx, instance.points.get(i).x);
			miny = Math.min(miny, instance.points.get(i).y);
			maxx = Math.max(maxx, instance.points.get(i).x);
			maxy = Math.max(maxy, instance.points.get(i).y);
		}
		
		double scaleX = 512.0 / Math.max(maxx-minx, 1.0);
		double scaleY = 512.0 / Math.max(maxy-miny, 1.0);
		double scale = Math.min(scaleX, scaleY);
		
		File file = new File(filename);
		try {
			PrintStream ps = new PrintStream(file);
			
			// ipe preamble stuff
			ps.println("<?xml version=\"1.0\"?>");
			ps.println("<!DOCTYPE ipe SYSTEM \"ipe.dtd\">");
			ps.println("<ipe version=\"70218\" creator=\"Ipe 7.2.21\">");
			ps.println("<info created=\"D:20210227165909\" modified=\"D:20210227165909\"/>"); // nobody cares about this data
			
			// ipe style
			ps.println("<ipestyle name=\"custom\">");
			ps.println("<symbol name=\"mark/disk(sx)\" transformations=\"translations\">");
			ps.println("<path fill=\"sym-stroke\">");
			ps.println("0.6 0 0 0.6 0 0 e");
			ps.println("</path>");
			ps.println("</symbol>");
			ps.println("</ipestyle>");
			
			// the page (actual stuff!)
			ps.println("<page>");
			
			// first the points
			for (int i = 0; i < N; i++) {
				ps.println("<use name=\"mark/disk(sx)\" pos=\"" + (scale * (instance.points.get(i).x - minx)) + " " + (scale * (instance.points.get(i).y - miny)) + " \" size=\"normal\" stroke=\"black\"/>");
			}
			
			// then the path
			ps.println("<path stroke=\"black\">");
			for (int i = 0; i < N; i++) {
				ps.println((scale * (instance.points.get(perm.get(i)).x - minx)) + " " + (scale * (instance.points.get(perm.get(i)).y - miny)) + " " + (i == 0 ? "m" : "l"));
			}
			ps.println("h");
			ps.println("</path>");
			
			ps.println("</page>");
			ps.println("</ipe>");
			
			ps.close();
		} catch (IOException e) {
			System.out.println("Could not write to output file!");
			e.printStackTrace();
		}		
		
	}
	
	
	
	// write solution to file
	public void output(String filename) {
		
		File file = new File(filename);
		try {
			PrintStream ps = new PrintStream(file);
			ps.println(N); // for checking
			// write the order of the points (using original IDs, so +1!)
			for (int i = 0; i < N; i++) {
				ps.println(perm.get(i)+1);
			}
			ps.close();
		} catch (IOException e) {
			System.out.println("Could not write to output file!");
			e.printStackTrace();
		}
		
	}

	
}
