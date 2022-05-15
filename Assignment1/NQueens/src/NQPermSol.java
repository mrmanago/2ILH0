import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
public class NQPermSol extends NQueenSol {

	ArrayList<Integer> rows;
	ArrayList<Integer> cols;

	// standard constructor
	public NQPermSol(NQueenInstance inst, boolean random) {
		super(inst);

		rows = new ArrayList<Integer>();
		cols = new ArrayList<Integer>();

		// Place queens on a diagonal so that there is 1 for each column and row
		for (int i = 0; i < N; i++) {
			rows.add(i);
			cols.add(i);
		}

		// Shuffle queens so that the position is random
		if (random) {
			Collections.shuffle(rows);
		}
	}
	
	
	
	// get the position of a queen
	public Pos getPosition(int i) {
		return new Pos(rows.get(i),cols.get(i));
	}

	// Swaps 2 selected row/columns
	public void applyLocalMove(int rc1, int rc2) {
		Collections.swap(rows, rc1, rc2);
	}

	// Undo a local move. must be executed after performing same move
	public void undoLocalMove(int rc1, int rc2) {
		applyLocalMove(rc1, rc2);
	}
}
