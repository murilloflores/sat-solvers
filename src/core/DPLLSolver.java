package core;

import java.util.ArrayList;
import java.util.List;

public class DPLLSolver implements Solver {

	@Override
	public boolean solve(List<Clause> clauses) {
		
		unitPropagation(clauses);
		if(clauses.isEmpty()) return true;
		if(containsEmptyClauses(clauses)) return false;
		
		Integer literal = selectLiteralForSpliting(clauses);
		Integer complementaryLiteral = literal * -1;
		
		if(solve(cloneAndAddClauseWithLiteral(clauses, literal)) == true) return true;
		else return solve(cloneAndAddClauseWithLiteral(clauses, complementaryLiteral));
		
	}

	private List<Clause> cloneAndAddClauseWithLiteral(List<Clause> clauses, Integer literal) {
		
		Clause clause = createClauseFromLiteral(literal);

		List<Clause> clone = clone(clauses);
		clone.add(clause);
		
		return clone;
	}

	private Clause createClauseFromLiteral(Integer literal) {
		
		ArrayList<Integer> literals = new ArrayList<Integer>();
		literals.add(literal);
		Clause clause = new Clause(literals);
		return clause;
		
	}

	private List<Clause> clone(List<Clause> clauses) {
		
		List<Clause> clone = new ArrayList<Clause>(clauses.size()+1);
		for(Clause clause: clauses){
			clone.add(new Clause(clause));
		}
		return clone;
		
	}

	private Integer selectLiteralForSpliting(List<Clause> clauses) {
		
		// no heuristics here
		return clauses.get(0).getLiterals().get(0);
		
	}

	private boolean containsEmptyClauses(List<Clause> clauses) {
		
		for(int i=0; i< clauses.size(); i++){
			Clause clause = clauses.get(i);
			if(clause.isEmpty()){
				return true;
			}
		}
		
		return false;
	}

	private void unitPropagation(List<Clause> clauses) {
		
		List<Integer> unitClausesLiterals = getUnitClausesLiterals(clauses);
		while(!unitClausesLiterals.isEmpty()){
			propagate(unitClausesLiterals, clauses);
			unitClausesLiterals = getUnitClausesLiterals(clauses);
		}
		
	}

	private List<Integer> getUnitClausesLiterals(List<Clause> clauses) {
		
		List<Integer> unitClausesLiterals = new ArrayList<Integer>();
		for(Clause clause: clauses){
			if(clause.isUnit()){
				unitClausesLiterals.add(clause.getLiterals().get(0));
			}
		}
		
		return unitClausesLiterals;
	}
	
	private void propagate(List<Integer> unitClausesLiterals, List<Clause> clauses) {
		
		for(Integer literal: unitClausesLiterals){
			propagateForUnitClause(literal, clauses);
		}
		
	}

	private void propagateForUnitClause(Integer literal, List<Clause> clauses) {
		
		Integer complementaryLiteral = literal * -1;
		
		for(int i=0; i<clauses.size(); i++){
			
			Clause clause = clauses.get(i);
			
			if(clause.containsLiteral(literal)){
				clauses.remove(i);
				i--;
				continue;
			}
			
			if(clause.containsLiteral(complementaryLiteral)){
				clause.removeLiteral(complementaryLiteral);
			}
			
		}
		
	}

}
