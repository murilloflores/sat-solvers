package solvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import parser.DimacsParser;
import representation.Clause;

public class DualSolver implements Solver {

	@Override
	public boolean solve(List<Clause> cnfClauses) {
		
		final Map<Integer, List<Integer>> quantumTable = buildQuantumTable(cnfClauses);
		
		List<Clause> openedDnfClauses = calculateInitialState(cnfClauses, quantumTable);
		
		while(!openedDnfClauses.isEmpty()){
		
			Clause currentState = getStateWithSmallestGap(cnfClauses, openedDnfClauses, quantumTable);
			
			if(isFinalState(currentState, cnfClauses, quantumTable)){
				return true;
			}
			
			//do the magic
			
		}

		return false;
	}
	
	private boolean isFinalState(Clause clause, List<Clause> cnfClauses, Map<Integer, List<Integer>> quantumTable) {
		List<Integer> gap = calculateGap(clause, cnfClauses, quantumTable);
		return gap.isEmpty();
	}

	private Clause getStateWithSmallestGap(List<Clause> cnfClauses, List<Clause> openedClauses, Map<Integer, List<Integer>> quantumTable) {
		
		int betterClauseIndex = 0;
		List<Integer> minorGap = calculateGap(openedClauses.get(0), cnfClauses, quantumTable);
		
		for(int i=1; i < openedClauses.size(); i++){
			
			Clause clause = openedClauses.get(i);
			List<Integer> gap = calculateGap(clause, cnfClauses, quantumTable);
			
			if(gap.size() < minorGap.size()){
				minorGap = gap;
				betterClauseIndex = i;
			}
			
		}
		
		return openedClauses.get(betterClauseIndex);
		
	}

	private List<Integer> calculateGap(Clause clause, List<Clause> cnfClauses, Map<Integer, List<Integer>> quantumTable) {

		Set<Integer> coveredClauses = new HashSet<Integer>();
		List<Integer> literals = clause.getLiterals();
		for(Integer literal: literals){
			
			List<Integer> coordinates = quantumTable.get(literal);
			for(Integer coordinate: coordinates){
				coveredClauses.add(coordinate);
			}
			
		}
		
		List<Integer> gap = new ArrayList<Integer>();
		for(int i=0; i< cnfClauses.size(); i++){
			if(!coveredClauses.contains(i)){
				gap.add(i);
			}
		}
		
		return gap;
	
	}

	private List<Clause> calculateInitialState(List<Clause> cnfClauses, Map<Integer, List<Integer>> quantumTable) {
	
		Clause clause = getBestCnfClauseToStart(cnfClauses, quantumTable);
		
		List<Clause> initialState = new ArrayList<Clause>();
		
		List<Integer> literals = clause.getLiterals();
		for(Integer literal: literals){
			
			ArrayList<Integer> clauseLiterals = new ArrayList<Integer>();
			clauseLiterals.add(literal);
			
			Clause initialStateClause = new Clause(clauseLiterals);

			initialState.add(initialStateClause);
		}
		
		
		return initialState;
	}

	private Clause getBestCnfClauseToStart(List<Clause> cnfClauses, Map<Integer, List<Integer>> quantumTable) {

		//Trying heuristic to determine the first clause to be used, i.e. the initial state (pg 25)
		
		Map<Integer, Set<Integer>> clausesCoverage = new HashMap<Integer, Set<Integer>>();
		
		for(int i=0; i< cnfClauses.size(); i++){
			Set<Integer> coverage = new HashSet<Integer>();

			Clause clause = cnfClauses.get(i);
			List<Integer> literals = clause.getLiterals();
			for(Integer literal: literals){
				List<Integer> coordinates = quantumTable.get(literal);
				for(Integer coordinate: coordinates){
					coverage.add(coordinate);
				}
			}
			clausesCoverage.put(i, coverage);
		}
		
		int maxCoverage = clausesCoverage.get(0).size();
		int bestClauseIndex = 0;
		for(int i=0; i< cnfClauses.size(); i++){
			
			if(clausesCoverage.get(i).size() > maxCoverage){
				maxCoverage = clausesCoverage.get(i).size();
				bestClauseIndex = i;
			}
			
		}
		
		return cnfClauses.get(bestClauseIndex);
	}

	private Map<Integer, List<Integer>> buildQuantumTable(List<Clause> clauses){
		
		//TODO Can be done in a better way
		
		Map<Integer, List<Integer>> quantumTable = new HashMap<Integer, List<Integer>>();
		
		for(int i=0;i<clauses.size();i++){
			
			Clause clause = clauses.get(i);
			
			List<Integer> literals = clause.getLiterals();
			for(Integer literal:literals){
				
				if(!quantumTable.containsKey(literal)){
					quantumTable.put(literal, new ArrayList<Integer>());
				}
				
				List<Integer> coordinates = quantumTable.get(literal);
				coordinates.add(i);
				
			}
			
		}
		
		return quantumTable;
		
	}
	
	public static void main(String[] args) throws IOException {
	
		DimacsParser parser = new DimacsParser();
		List<Clause> clauses = parser.parse("examples/dual_example_modificated.cnf");
		
		Solver solver =  new DualSolver();
		
		solver.solve(clauses);
		
	}
	
	
}
