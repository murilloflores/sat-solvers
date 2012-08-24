package core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import parser.DimacsParser;

public class DPLLSolver implements Solver {

	@Override
	public boolean solve(List<Clause> clauses) {
		
		unitPropagation(clauses);
		if(clauses.isEmpty()) return true;
		if(containsEmptyClauses(clauses)) return false;
		
		throw new RuntimeException("ERROR");
		
	}

	private boolean containsEmptyClauses(List<Clause> clauses) {
		
		for(Clause clause: clauses){
			if(clause.isEmpty()) return true;
		}
		
		return false;
	}

	private void unitPropagation(List<Clause> clauses) {
		
		List<Clause> unitClauses = getUnitClauses(clauses);
		while(!unitClauses.isEmpty()){
			propagate(unitClauses, clauses);
			unitClauses = getUnitClauses(clauses);
		}
		
	}

	private List<Clause> getUnitClauses(List<Clause> clauses) {
		
		List<Clause> unitClauses = new ArrayList<Clause>();
		for(Clause clause: clauses){
			if(clause.isUnit()){
				unitClauses.add(clause);
			}
		}
		
		return unitClauses;
	}
	
	private void propagate(List<Clause> unitClauses, List<Clause> clauses) {
		
		for(Clause unitClause: unitClauses){
			propagateForUnitClause(unitClause, clauses);
		}
		
	}

	private void propagateForUnitClause(Clause unitClause, List<Clause> clauses) {
		
		Integer literal = unitClause.getLiterals().get(0);
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

	public static void main(String[] args) throws IOException {
		
		DimacsParser parser = new DimacsParser();
		List<Clause> clauses = parser.parse("/home/murillo/Projects/tcc/sat-solvers/examples/minimun_satisfiable.cnf");
		
		DPLLSolver solver =  new DPLLSolver();
		System.out.println(solver.solve(clauses));
		
	}

}
