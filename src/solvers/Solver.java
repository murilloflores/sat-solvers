package solvers;

import java.util.List;

import representation.Clause;
import representation.Theory;


public interface Solver {

	boolean isSatisfiable(List<Clause> clauses, Integer numberOfVariables);
	
	List<Clause> toMinimalDualClauses(Theory theory);
	
}
