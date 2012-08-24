package core;

import java.util.List;

public interface Solver {

	boolean solve(List<Clause> clauses);
	
}
