package solvers;

import java.util.List;

import representation.Clause;


public interface Solver {

	boolean solve(List<Clause> clauses);
	
}
