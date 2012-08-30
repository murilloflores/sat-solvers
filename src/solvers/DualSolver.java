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
import representation.Quantum;
import representation.QuantumTable;
import representation.SearchState;

public class DualSolver implements Solver {

	private List<Clause> cnfClauses;
	
	@Override
	public boolean solve(List<Clause> clauses) {
		
		this.cnfClauses = clauses;
		
		QuantumTable quantumTable = buildQuantumTable();
		
		List<SearchState> openedStates = calculateInitialStates(quantumTable);
		List<SearchState> closedStates = new ArrayList<SearchState>();
		
		while(!openedStates.isEmpty()){
		
			SearchState currentState = getStateWithSmallestGap(openedStates);

			if(isFinalState(currentState)){
				return true;
			}
			
			openedStates.remove(currentState);
			closedStates.add(currentState);
			
			System.out.println("Current "+ currentState);
			System.out.println("OPENED " +openedStates);
			System.out.println("CLOSED " +closedStates);
			
			List<SearchState> neighbors = calculateNehighBors(currentState);
			
			//do some checks?
			
			for(SearchState state: neighbors){
				openedStates.add(state);
			}
			
			
		}

		return false;
	}
	
	private List<SearchState> calculateNehighBors(SearchState currentState) {
		// FIXME Sucessors function implemented from pg 25
		return null;
	}

	private boolean isFinalState(SearchState state) {
		return calculateGap(state).isEmpty();
	}

	private SearchState getStateWithSmallestGap(List<SearchState> openedStates) {
		
		int betterStateIndex = 0;
		List<Integer> minorGap = calculateGap(openedStates.get(0));
		
		for(int i=1; i < openedStates.size(); i++){
			
			SearchState state = openedStates.get(i);
			List<Integer> gap = calculateGap(state);
			
			if(gap.size() < minorGap.size()){
				minorGap = gap;
				betterStateIndex = i;
			}
			
		}
		
		return openedStates.get(betterStateIndex);
		
	}

	private List<Integer> calculateGap(SearchState searchState) {

		Set<Integer> coveredClauses = new HashSet<Integer>();
		
		for(Quantum quantum: searchState.getQuantums()){
			for(Integer coordinate: quantum.getCoordinates()){
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

	private List<SearchState> calculateInitialStates(QuantumTable quantumTable) {
	
		Clause clause = getBestCnfClauseToStart(quantumTable);
		
		List<SearchState> initialState = new ArrayList<SearchState>();
		
		List<Integer> literals = clause.getLiterals();
		for(Integer literal: literals){
			
			SearchState state = new SearchState();
			state.addQuantum(quantumTable.getQuantum(literal));
			
			initialState.add(state);
		}
		
		
		return initialState;
	}

	private Clause getBestCnfClauseToStart(QuantumTable quantumTable) {

		//Trying heuristic to determine the first clause to be used, i.e. the initial state (pg 25)
		
		Map<Integer, Set<Integer>> clausesCoverage = new HashMap<Integer, Set<Integer>>();
		
		for(int i=0; i< cnfClauses.size(); i++){
			Set<Integer> coverage = new HashSet<Integer>();

			Clause clause = cnfClauses.get(i);
			List<Integer> literals = clause.getLiterals();
			for(Integer literal: literals){
				Quantum quantum = quantumTable.getQuantum(literal);
				
				for(Integer coordinate: quantum.getCoordinates()){
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

	private QuantumTable buildQuantumTable(){
		
		QuantumTable table = new QuantumTable();
		
		for(int i=0;i<cnfClauses.size();i++){
			
			Clause clause = cnfClauses.get(i);
			
			List<Integer> literals = clause.getLiterals();
			for(Integer literal:literals){

				table.addCoordinate(literal, i);
				
			}
			
		}
		
		return table;
		
	}
	
	public static void main(String[] args) throws IOException {
	
		DimacsParser parser = new DimacsParser();
		List<Clause> clauses = parser.parse("examples/dual_example_modificated.cnf");
		
		Solver solver =  new DualSolver();
		
		solver.solve(clauses);
		
	}
	
	
}
