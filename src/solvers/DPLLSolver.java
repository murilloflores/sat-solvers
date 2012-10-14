package solvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import parser.DimacsParser;

import representation.Clause;
import representation.Theory;



public class DPLLSolver implements Solver {

	@Override
	public boolean isSatisfiable(List<Clause> clauses, Integer numberOfVariables) {
		
		unitPropagation(clauses);
		if(clauses.isEmpty()) return true;
		if(containsEmptyClauses(clauses)) return false;
		
		Integer literal = selectLiteralForSpliting(clauses);
		Integer complementaryLiteral = literal * -1;
		
		// BBO Heuristics can be applied here
		if(isSatisfiable(cloneAndAddClauseWithLiteral(clauses, literal), numberOfVariables) == true) return true;
		else return isSatisfiable(cloneAndAddClauseWithLiteral(clauses, complementaryLiteral), numberOfVariables);
		
	}

	@Override
	public List<Clause> toMinimalDualClauses(List<Clause> clauses, Integer numberOfVariables) {
		// TODO Auto-generated method stub
		return null;
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
		// return clauses.get(0).getLiterals().get(0);
		
		// MOMS - Max Ocurrences on the Minimun Lenght clauses
		int shortestClausesLenght = calculateShortestClausesLenght(clauses);
		
		Map<Integer, Integer> countOcurrencesByLiteral = new HashMap<Integer, Integer>();
		
		for(Clause clause: clauses){
			if(clause.getLiterals().size() == shortestClausesLenght){
				List<Integer> literals = clause.getLiterals();
				for(Integer literal:literals){
					
					if(!countOcurrencesByLiteral.containsKey(literal)){
						countOcurrencesByLiteral.put(literal, 0);
					}
					
					Integer oldValue = countOcurrencesByLiteral.get(literal);
					countOcurrencesByLiteral.put(literal, oldValue + 1);
					
				}
			}
		}
		
		Integer maxOcurrences = null;
		Integer maxFrequencyLiteral = null;
		
		for(Map.Entry<Integer, Integer> entry: countOcurrencesByLiteral.entrySet()){
			
			Integer literal = entry.getKey();
			Integer literalOcurrences = entry.getValue();
			
			Integer complementaryLiteral = literal * -1;
			Integer complementaryLiteralOcurrences = (countOcurrencesByLiteral.containsKey(complementaryLiteral)) ? countOcurrencesByLiteral.get(complementaryLiteral) : 0;
			
			Integer totalOcurrences = literalOcurrences + complementaryLiteralOcurrences;
			
			if(maxOcurrences == null){
				maxOcurrences = totalOcurrences;
				maxFrequencyLiteral = literal; 
				continue;
			}
			
			if(totalOcurrences > maxOcurrences){
				maxOcurrences = totalOcurrences;
				maxFrequencyLiteral = literal;
			}
			
		}
		
		return maxFrequencyLiteral;
		
	}

	private int calculateShortestClausesLenght(List<Clause> clauses) {
		
		Integer shortestClausesLenght = null;
		
		for(Clause clause: clauses){
			
			if(shortestClausesLenght == null && !clause.isEmpty() ){
				shortestClausesLenght = clause.getLiterals().size();
				continue;
			}
			
			if(!clause.isEmpty() && clause.getLiterals().size() < shortestClausesLenght){
				shortestClausesLenght = clause.getLiterals().size();
			}
		}
		
		return shortestClausesLenght;
		
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
	
	public static void main(String[] args) throws IOException {
		
		DimacsParser parser = new DimacsParser();
		Theory theory = parser.parse("examples/uuf75-024.cnf");
		
		Solver solver =  new DPLLSolver();
		
		System.out.println(solver.isSatisfiable(theory.getClauses(), theory.getNumberOfVariables()));
		
	}

}
