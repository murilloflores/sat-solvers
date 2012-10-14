package solvers;

import java.util.List;

import representation.Clause;
import representation.Theory;


public interface Solver {

	boolean isSatisfiable(Theory theory);
	
	List<Clause> toMinimalDualClauses(Theory theory);
	
}
