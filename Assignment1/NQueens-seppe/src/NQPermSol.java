import java.util.ArrayList;
import java.util.Random;

public class NQPermSol extends NQueenSol {

	// TODO: needs a solution representation
	ArrayList<Integer> rows; // permutation of the rows 
	ArrayList<Pos> queens; // positions of all queens

	// standard constructor
	public NQPermSol(NQueenInstance inst, boolean random) {
		super(inst);

		// TODO: fill in the rest of the constructor
		rows = new ArrayList<Integer>(); // row positions
		queens = new ArrayList<Pos>(); // queen positions
		
		// Initialize the rows and columns to their standard values
		for (int i = 0; i < inst.N ; i++) {
			rows.add(i);
		}

		if (!random) {
			// Place all queens on the diagonal
			for (int i = 0; i < N; i++) {
				queens.add(new Pos(i, i));
			}
		}
		else {
			// Place queens randomly (one queen per row and column)
			Random rand = new Random();
			for (int i = 0; i < N; i++) {
				boolean taken = false;
				int y = rand.nextInt(N);
				for (int j = 0; j < i; j++) {
					if(queens.get(j).y == y) {
						taken = true;
					}
				}

				// Run again if this position was already taken
				if (taken) {
					i = i - 1;
					continue;
				}

				// We are safe to add the queen
				queens.add(new Pos(i, y));
			}			
		}
	}
	
	
	
	// get the position of a queen
	public Pos getPosition(int i) {
		
		// TODO: must be filled in! Should return position of i^th queen
		Pos pos = queens.get(i);
		int x = pos.x;
		int y = rows.get(pos.y);
		return new Pos(x, y);
	}
	
	
	// TODO: Don't forget to implement a local move (along with the undo)!
	// Interchange the rows or columns rID1 and rID2, depending on [row].
	public boolean applyLocalMove(int rID1, int rID2) {
		if (rID1 >= 0 && rID2 >= 0 && rID1 < N && rID2 < N) {
			int temp = rows.get(rID1);
			rows.set(rID1, rows.get(rID2));
			rows.set(rID2, temp); 
			return true;
		}
		else return false;
	}
	
	// Undo a local move (must be executed after performing the same local move for correct behavior)
	public void undoLocalMove(int rID1, int rID2) {
		applyLocalMove(rID1, rID2);
	}
}
