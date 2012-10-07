package solvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import parser.DimacsParser;
import representation.Clause;



public class DPLLSolver implements Solver {

	@Override
	public boolean isSatisfiable(List<Clause> clauses) {
		
		unitPropagation(clauses);
		if(clauses.isEmpty()) return true;
		if(containsEmptyClauses(clauses)) return false;
		
		Integer literal = selectLiteralForSpliting(clauses);
		Integer complementaryLiteral = literal * -1;
		
		// BBO Heuristics can be applied here
		if(isSatisfiable(cloneAndAddClauseWithLiteral(clauses, literal)) == true) return true;
		else return isSatisfiable(cloneAndAddClauseWithLiteral(clauses, complementaryLiteral));
		
	}

	private List<Integer> firstResultDpll(List<Clause> clauses){
		
		List<Integer> unitPropagatedLiterals = unitPropagation(clauses);
		if(clauses.isEmpty()) return unitPropagatedLiterals;
		if(containsEmptyClauses(clauses)) return Collections.emptyList();
		
		Integer literal = selectLiteralForSpliting(clauses);
		Integer complementaryLiteral = literal * -1;
		
		// BBO Heuristics can be applied here
		List<Integer> firstResultDpllWithLiteral = firstResultDpll(cloneAndAddClauseWithLiteral(clauses, literal));
		if(!firstResultDpllWithLiteral.isEmpty()) {
			firstResultDpllWithLiteral.addAll(unitPropagatedLiterals);
			return firstResultDpllWithLiteral;
		}
		
		List<Integer> firstResultDpllWithComplementaryLiteral = firstResultDpll(cloneAndAddClauseWithLiteral(clauses, complementaryLiteral));
		if(!firstResultDpllWithComplementaryLiteral.isEmpty()){
			firstResultDpllWithComplementaryLiteral.addAll(unitPropagatedLiterals);
			return firstResultDpllWithComplementaryLiteral;
		}
		
		return Collections.emptyList();
		
	}
	
	private Set<List<Integer>> allResultsDpll(List<Clause> clauses){
		
		Set<List<Integer>> resultList = new HashSet<List<Integer>>();

		List<Integer> unitPropagatedLiterals = unitPropagation(clauses);
		if(clauses.isEmpty()) {
			resultList.add(unitPropagatedLiterals);
			return resultList;
		}
		
		if(containsEmptyClauses(clauses)) return Collections.emptySet();
		
		
		Set<Integer> remainingLiterals = remainingLiterals(clauses);
		for(Integer literal: remainingLiterals){
			Set<List<Integer>> results = allResultsDpll(cloneAndAddClauseWithLiteral(clauses, literal));
			for(List<Integer> result: results){
				result.addAll(unitPropagatedLiterals);
				resultList.addAll(results);
			}
		}
		
		return resultList;
		
	}
	
	private Set<Integer> remainingLiterals(List<Clause> clauses) {
		
		Set<Integer> remainingLiterals = new HashSet<Integer>();
		for(Clause clause: clauses){
			for(Integer literal: clause.getLiterals()){
				remainingLiterals.add(literal);
			}
		}
		
		
		return remainingLiterals;
	}

	@Override
	public List<Clause> toMinimalDualClauses(List<Clause> clauses) {
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

	private List<Integer> unitPropagation(List<Clause> clauses) {
		
		List<Integer> unitClauseLiteralsAll = new ArrayList<Integer>();
		
		List<Integer> unitClausesLiterals = getUnitClausesLiterals(clauses);
		while(!unitClausesLiterals.isEmpty()){
			unitClauseLiteralsAll.addAll(unitClausesLiterals);
			propagate(unitClausesLiterals, clauses);
			unitClausesLiterals = getUnitClausesLiterals(clauses);
		}
		
		return unitClauseLiteralsAll;
		
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
		List<Clause> clauses = parser.parse("examples/dual_example.cnf");
		
		DPLLSolver solver =  new DPLLSolver();
		
		System.out.println(solver.allResultsDpll(clauses));
		
	}

}
